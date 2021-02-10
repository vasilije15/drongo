package dsd.mdhfer.drongo.utilitiesTest;

import androidx.annotation.VisibleForTesting;

import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import dsd.mdhfer.drongo.utility.CryptoUtil;
import dsd.mdhfer.drongo.utility.Utility;

import static dsd.mdhfer.drongo.utility.Utility.validatePassword;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    String PASS_TEST = "test";
    String PASS_HASHTEST = "1000:6347397a6232787058326468:c8824db24fbb7ba455888aa1c5a85" +
            "602fb1adc084635f3256d2e6e3cc7523a4340a51" +
            "15699d26ae64658692d9509c2016d244e6e9c1b1c33c531d88af9498f6b";
    String PASS_HASHTEST_WRONG = "1000:6347397a6232787058326468:c8824db24fbb8ba455888aa1c5a85" +
            "602fb1adc084635f3256d2e6e3cc7523a2340a51" +
            "15699d26ae64658692d9509c2016d244e6e9c1b8c33c531d88af9498f6b";


    @Test
    public void generatePasswordHashTest () throws InvalidKeySpecException, NoSuchAlgorithmException {
        String testHash= Utility.generatePasswordHash(PASS_TEST);
        assertEquals(PASS_HASHTEST, testHash);
    }

    @Test
    public void validatePasswordTest_Valid() throws InvalidKeySpecException, NoSuchAlgorithmException {
        boolean result = validatePassword(PASS_TEST, PASS_HASHTEST);
        assertTrue(result);
    }

    @Test
    public void validatePasswordTest_Invalid() throws InvalidKeySpecException, NoSuchAlgorithmException {
        boolean result = validatePassword("tesr", PASS_HASHTEST);
        assertFalse(result);
    }

    @Test
    public void validatePasswordTest_Invalid2() throws InvalidKeySpecException, NoSuchAlgorithmException {
        boolean result = validatePassword(PASS_TEST, PASS_HASHTEST_WRONG);
        assertFalse(result);
    }

    @Test
    public void generateKeyPairTest()
            throws NoSuchAlgorithmException,
                NoSuchPaddingException,
                InvalidKeyException,
                IllegalBlockSizeException,
                BadPaddingException {
        Map<String, String> keyMap = new HashMap<>();
        keyMap = CryptoUtil.generateKeyPair();
        assertTrue(keyMap.containsKey("privateKey"));
        assertTrue(keyMap.containsKey("publicKey"));
    }

    @Test(expected = NullPointerException.class)
    public void encrypt_decrypt_Test()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeySpecException {

        Map<String, String> keyMap = CryptoUtil.generateKeyPair();
        
        String toBeEncryped = "This text will be encrypted !";
        String encryptedText = CryptoUtil.encrypt(toBeEncryped, keyMap.get("publicKey"));

        assertNotNull(encryptedText);

        String decryptedText = CryptoUtil.decrypt(encryptedText, keyMap.get("privateKey"));

        assertEquals(decryptedText, toBeEncryped);
    }

}
