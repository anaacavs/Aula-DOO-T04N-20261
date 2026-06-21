public class JsonUtil {

    private String texto;
    private int pos;

    public JsonUtil(String texto) {
        this.texto = texto;
        this.pos = 0;
    }

    private void pularEspacos() {
        while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) {
            pos++;
        }
    }

    private String parseString() {
        pularEspacos();
        pos++;
        StringBuilder sb = new StringBuilder();
        while (texto.charAt(pos) != '"') {
            sb.append(texto.charAt(pos));
            pos++;
        }
        pos++;
        return sb.toString();
    }

    public static Object parse(String json) {
        JsonUtil j = new JsonUtil(json);
        return j.parseString();
    }
}