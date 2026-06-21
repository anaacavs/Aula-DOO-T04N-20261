import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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

    private Object parseValor() {
        pularEspacos();
        char c = texto.charAt(pos);
        if (c == '"') {
            return parseString();
        } else if (c == '{') {
            return parseObjeto();
        } else if (c == '[') {
            return parseArray();
        } else {
            return parseLiteral();
        }
    }

    private Map<String, Object> parseObjeto() {
        Map<String, Object> mapa = new HashMap<>();
        pos++;
        pularEspacos();
        if (texto.charAt(pos) == '}') {pos++; return mapa;}
        while (true) {
            pularEspacos();
            String chave = parseString();
            pularEspacos();
            pos++;
            Object valor = parseValor();
            mapa.put(chave, valor);
            pularEspacos();
            if (texto.charAt(pos) == ',') {
                pos++;
            } else {
                pos++;
                break;
            }
        }
        return mapa;
    } 

        private List<Object> parseArray() {
        List<Object> lista = new ArrayList<>();
        pos++;
        pularEspacos();
        if (texto.charAt(pos) == ']') { pos++; return lista; }
        while (true) {
            Object valor = parseValor();
            lista.add(valor);
            pularEspacos();
            if (texto.charAt(pos) == ',') {
                pos++;
            } else {
                pos++;
                break;
            }
        }
        return lista;
    }

    private Object parseLiteral() {
        pularEspacos();
        StringBuilder sb = new StringBuilder();
        while (pos < texto.length()) {
            char c = texto.charAt(pos);
            if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                break;
            }
            sb.append(c);
            pos++;
        }
        String valor = sb.toString();
        if (valor.equals("true")) return true;
        if (valor.equals("false")) return false;
        if (valor.equals("null")) return null;
        return Double.parseDouble(valor);
    }

    public static Object parse(String json) {
        JsonUtil j = new JsonUtil(json);
        return j.parseValor();
    }
}