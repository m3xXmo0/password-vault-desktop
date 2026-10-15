package de.mo.vault;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.crypto.SecretKey;



class VaultStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAndLoadReturnsSameEntries() throws Exception {
        Path file = tempDir.resolve("vault.dat");
        Vault vault = new Vault();
        vault.add(new Entry("GitHub", "mo", "pw1"));
        vault.add(new Entry("Netflix", "maxi", "pw2"));

        VaultStorage.save(vault, file, "master".toCharArray());
        Vault loaded = VaultStorage.load(file, "master".toCharArray()).vault();

        assertEquals(2, loaded.getEntries().size());
        assertEquals("pw2", loaded.findByService("Netflix").getPassword());
    }

    @Test
    void wrongMasterPasswordIsRejected() throws Exception {
        Path file = tempDir.resolve("vault.dat");
        VaultStorage.save(new Vault(), file, "richtig".toCharArray());

        assertThrows(VaultException.class,
                () -> VaultStorage.load(file, "falsch".toCharArray()));
    }

    @Test
    void manipulatedFileIsDetected() throws Exception {
        Path file = tempDir.resolve("vault.dat");
        Vault vault = new Vault();
        vault.add(new Entry("GitHub", "mo", "pw1"));
        VaultStorage.save(vault, file, "master".toCharArray());

        String content = Files.readString(file);
        char[] chars = content.toCharArray();
        int pos = chars.length / 2;
        chars[pos] = (chars[pos] == 'A') ? 'B' : 'A';   // ein Zeichen in der Mitte ändern
        Files.writeString(file, new String(chars));

        assertThrows(VaultException.class,
                () -> VaultStorage.load(file, "master".toCharArray()));
    }

    @Test
    void tooShortFileIsRejected() throws Exception {
        Path file = tempDir.resolve("vault.dat");
        Files.writeString(file, "AAAA");

        assertThrows(VaultException.class,
                () -> VaultStorage.load(file, "master".toCharArray()));
    }

    @Test
    void missingFileGivesEmptyVault() throws Exception {
        Path file = tempDir.resolve("gibt-es-nicht.dat");
        Vault loaded = VaultStorage.load(file, "master".toCharArray()).vault();
        assertEquals(0, loaded.getEntries().size());
    }

    @Test
    void fileShorterThanHeaderIsRejected() throws Exception {
        Path file = tempDir.resolve("vault.dat");
        Files.writeString(file, java.util.Base64.getEncoder().encodeToString(new byte[20]));

        assertThrows(VaultException.class,
                () -> VaultStorage.load(file, "master".toCharArray()));
    }
    @Test
    void entryWithEmptyPasswordSurvivesSaveAndLoad() throws Exception {
        Path file = tempDir.resolve("vault.dat");
        Vault vault = new Vault();
        vault.add(new Entry("Mail", "mo", ""));

        VaultStorage.save(vault, file, "master".toCharArray());
        Vault loaded = VaultStorage.load(file, "master".toCharArray()).vault();

        assertEquals(1, loaded.getEntries().size());
    }

    @Test
    void saveLeavesNoTempFileBehind() throws Exception{
        Path file = tempDir.resolve("vault.dat");

        VaultStorage.save(new Vault(), file, "master".toCharArray());
        assertTrue(Files.exists(file));

        VaultStorage.save(new Vault(), file, "master".toCharArray());
        assertTrue(Files.exists(file));

        assertFalse(Files.exists(tempDir.resolve("vault.dat.tmp")));
    }

    @Test
    void previousVersionIsKeptAsBackup() throws Exception {
        Path file = tempDir.resolve("vault.dat");
        Path backup = tempDir.resolve("vault.dat.bak");

        Vault first = new Vault();
        first.add(new Entry("GitHub", "mo", "alt"));
        VaultStorage.save(first, file, "master".toCharArray());

        Vault second = new Vault();
        second.add(new Entry("GitHub", "mo", "neu"));
        VaultStorage.save(second, file, "master".toCharArray());

        assertTrue(Files.exists(backup));
        Vault fromBackup = VaultStorage.load(backup, "master".toCharArray()).vault();
        assertEquals("alt", fromBackup.findByService("GitHub").getPassword());
    }

    @Test
    void duplicateEntryInFileProducesWarning() throws Exception{
        Path file = tempDir.resolve("vault.dat");

        // Datei von Hand bauen: Salt + verschl. Text mit doppeltem Dienst
        byte[] salt = CryptoService.generateSalt();
        SecretKey key = CryptoService.deriveKey("master".toCharArray(), salt);
        byte[] encrypted = CryptoService.encrypt("GitHub\tmo\tpw1\ngithub\tmo\tw2\n", key);

        byte[] data = new byte[salt.length + encrypted.length];
        System.arraycopy(salt, 0, data, 0, salt.length);
        System.arraycopy(encrypted, 0, data, salt.length, encrypted.length);
        Files.writeString(file, java.util.Base64.getEncoder().encodeToString(data));

        LoadResult result = VaultStorage.load(file, "master".toCharArray());

        assertEquals(1, result.vault().getEntries().size());
        assertEquals(1, result.warnings().size());
    }
    @Test
    void malformedLineProducesWarning() throws Exception {
        Path file = tempDir.resolve("vault.dat");

        byte[] salt = CryptoService.generateSalt();
        SecretKey key = CryptoService.deriveKey("master".toCharArray(), salt);
        byte[] encrypted = CryptoService.encrypt("GitHub\tmo\tpw1\tgithub\tmo\tpw2\n", key);
        byte[] data = new byte[salt.length + encrypted.length];
        System.arraycopy(salt, 0, data, 0, salt.length);
        System.arraycopy(encrypted, 0, data, salt.length, encrypted.length);
        Files.writeString(file, java.util.Base64.getEncoder().encodeToString(data));

        LoadResult result = VaultStorage.load(file, "master".toCharArray());

        assertEquals(0, result.vault().getEntries().size());
        assertEquals(1, result.warnings().size());
    }
}