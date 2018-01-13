package io.github.bswearteam.bswear;

public class test {
    public static void main(String[] args) {
        ifHasWord0("test test", "test");
        //System.out.println(a("test c Abc"));
    }
    
    public static String a(String str) {
        return str.replaceAll("[^\\p{L}\\p{Nd}]", "");//.replaceAll("[*.-=+:]", "").replaceAll("[%&*()$#!-_@]", "");
    }

    public static boolean ifHasWord0(String message, String word) {
        boolean a = false;
        String[] messageAsArray = message.split("[ ]");
        for (String partOfMessage : messageAsArray) {
            StringBuilder strBuilder = new StringBuilder();
            char[] messageAsCharArray = partOfMessage.toLowerCase().toCharArray();
            for (char character : messageAsCharArray) {
                if (character >= '0' && character <= '9' || character >= 'a' && character <= 'z')
                    strBuilder.append(character);
                System.out.println(character);
            }
            if (strBuilder.toString().equalsIgnoreCase(word)) a = true;
            System.out.println(strBuilder.toString() + a);
        }
        return a;
    }
}
