public class Homework{

    public static boolean twoOccurrences(String stringA, String stringB) {
        int currPos;
        int lenA = stringA.length();

        int nextPos = 0;
        int count = 0;

        while((currPos = stringB.indexOf(stringA, nextPos)) >= 0){
            nextPos = currPos + lenA;
            count += 1;
            System.out.println("String a found starting at char: " +  currPos);
            if(count >= 2){
                return true;
            }
        }
        return false;
    }

    public static String afterFirst(String stringA, String stringB){
        int lenA = stringA.length();
        int firstOccurrence = stringB.indexOf(stringA);
        
        if (firstOccurrence >= 0){
            return stringB.substring(firstOccurrence + lenA);
        }
        return stringB;
    }

    public static void main(String argv[]){
        System.out.println(afterFirst("a", "banana"));
        System.out.println(afterFirst("ana", "banana"));
        System.out.println(afterFirst("ba", "banana"));
        // System.out.println(twoOccurrences("a","banana"));
        // System.out.println(twoOccurrences("ba","banana"));
        // System.out.println(twoOccurrences("na","banana"));
        // System.out.println(twoOccurrences("n","banana"));
    }
    

 
}