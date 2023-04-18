import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class menu {
    private static BigInteger e; // The public exponent
    private static BigInteger d; // The private exponent
    private static BigInteger n; // The modulus

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Create a scanner object to read user input from the console

        while (true) {
            System.out.println("\n RSA Menu: );
            System.out.println("1. Q1 Generate RSA key pair");
            System.out.println("2. Q2 Encrypt");
            System.out.println("3. Q3 Decrypt");
            System.out.println("4. Exit");
            System.out.print("Please input a single digit from the options above (1-4) : ");
            if (scanner.hasNextInt()) { // Check if the user input is an integer
                int choice = scanner.nextInt(); // Read the user input
                try { // Try to execute the code in the try block and catch any exceptions thrown by the encrypt and decrypt methods
                    switch (choice) { // Switch case to execute the code based on the user input
                        case 1:
                            System.out.print("Please input the key size (in bits, only works for 64 bits/5 letters above currently): ");
                            if (scanner.hasNextInt()) { // Check if the user input is an integer
                                int keySize = scanner.nextInt();
                                if (keySize >= 64) { // Check if the key size is greater than or equal to 64 bits
                                    generateKeyPair(keySize); // Generate the key pair
                                    System.out.println("Public key (e, n): (" + e + ", " + n + ")"); // Print the public key to the console
                                    System.out.println("Private key (d, n): (" + d + ", " + n + ")"); // Print the private key to the console
                                } else {
                                    System.out.println("Invalid input! Please input a key size of 64 bits or greater.");
                                }
                            } else {
                                System.out.println("Invalid input! Please input a number for the key size.");
                                scanner.next(); // Clear the invalid input
                            }
                            break; // Break out of the switch case
                        case 2:
                            if (e == null || n == null) { // If the user tries to encrypt without generating a key pair first
                                System.out.println("Please generate a key pair first!");
                                break;
                            }
                            System.out.print("Please input the message to be encrypted: "); // Read the message from the user
                            scanner.nextLine();
                            String message = scanner.nextLine(); // Read the message from the user
                            boolean isAlphaNumeric = message.chars().allMatch(Character::isLetterOrDigit)
                                    || message.chars().anyMatch(Character::isSpaceChar); // Check if the message is alphanumeric
                            if (isAlphaNumeric) { // If the message is alphanumeric, encrypt it
                                BigInteger encryptedMessage = encrypt(message); // Encrypt the message
                                System.out.println("Encrypted message (copy this -->): " + encryptedMessage); // Print the encrypted message
                            } else {
                                throw new IllegalArgumentException("Input only alphanumeric characters"); // Throw an exception if the message is not alphanumeric
                            }
                            break;
                        case 3:
                            if (d == null || n == null) { // If the user tries to decrypt without generating a key pair first
                                System.out.println("Please generate a key pair first!");
                                break;
                            }
                            System.out.print("Please input the message to be decrypted ( paste the encrypted message here-->) : ");
                            scanner.nextLine(); // Clear the newline character from the previous input
                            String ciphertext = scanner.nextLine(); // Read the ciphertext from the user
                            BigInteger m1 = new BigInteger(ciphertext); // Convert the ciphertext to a BigInteger
                            BigInteger c = new BigInteger(m1.toString()); // Convert the ciphertext to a BigInteger
                            String decryptedMessage = decrypt(c);   // Decrypt the ciphertext
                            System.out.println("Decrypted message: " + decryptedMessage); // Print the decrypted message
                            break;
                        case 4:
                            System.out.println("Goodbye! Program Exiting..."); // Print a goodbye message to the console and exit the program
                            System.exit(0); // Exit the program with a status code of 0 (success)
                            break;
                        default:
                            System.out.println("Invalid input! Please try again. Enter a digit between 1-4 "); // If the user input is not a single digit from 1 to 4
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input! Please try again."); // Catch the exception thrown by the encrypt and decrypt methods
                }
            } else {
                System.out.println("Invalid input! Please enter a number options between (1-4).");
                scanner.next(); // Clear the invalid input
            }
        }
    }
    //Generate the key pair with the key size specified by the user and assign the values to the public and private keys (e, d, n) respectively and print them out to the console
    public static void generateKeyPair(int keySize) {
        SecureRandom random = new SecureRandom(); // Create a secure random object to generate random numbers for the key pair generation process
        BigInteger p = BigInteger.probablePrime(keySize / 2, random); // p is a random prime number
        BigInteger q = BigInteger.probablePrime(keySize / 2, random); // q is a random prime number
        n = p.multiply(q); // n is the same for both public and private keys (n = p * q)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // phi is the same for both public and private keys (phi = (p - 1) * (q - 1))
        e = BigInteger.valueOf(1); // e is the public key (e is a random number)
        BigInteger t = phi.subtract(BigInteger.TWO); // t is the value of phi - 2

        for (BigInteger bi = t; bi.compareTo(BigInteger.ZERO) > 0; // Loop to find the value of e
             bi = bi.subtract(BigInteger.ONE)) { // Loop to find the value of e
            int res = (phi.gcd(bi)).compareTo(e); // Should return zero if gcd is equal to 1
            if (res == 0) { // if gcd is equal to 1 then break the loop and assign the value of bi to e
                // (public key) and continue with the program execution
                e = bi; // e is the public key
                break; // break the loop
            }
        }
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) { // e must be less than phi and e must be coprime with phi
            e = e.add(BigInteger.ONE);  // e is incremented until it is coprime with phi
        }
        d = e.modInverse(phi); // d is the private key (d is the multiplicative inverse of e mod phi)
    }
    // Encrypt the message using the public key (e, n) and return the encrypted message as a BigInteger object
    public static BigInteger encrypt(String message) {
        return new BigInteger(message.getBytes()).modPow(e, n); // Encrypt the message using the public key
    }
    // Decrypt the message using the private key (d, n) and return the decrypted message as a String object
    public static String decrypt(BigInteger encryptedMessage) {
        return new String(encryptedMessage.modPow(d, n).toByteArray()); // Decrypt the message using the private key
    }
}
