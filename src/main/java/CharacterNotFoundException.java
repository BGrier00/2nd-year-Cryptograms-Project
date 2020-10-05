public class CharacterNotFoundException extends Exception {

    private final String c;

    CharacterNotFoundException(Object c) {
        this.c = c.toString();
    }

    public String toString() {
        return "Character " + c + " does not exist in this cryptogram";
    }
}
