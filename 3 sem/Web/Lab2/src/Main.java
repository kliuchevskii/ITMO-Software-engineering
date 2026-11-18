import com.fastcgi.FCGIInterface;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Main {

    static ArrayList<String> history = new ArrayList<>();

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        while (fcgi.FCGIaccept() >= 0) {
            long start = System.nanoTime();
            try {
                String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
                if (method.equals("GET")) {
                    sendHistory(start);                  // при загрузке страницы
                } else if (method.equals("POST")) {
                    handlePost(readBody(), start);
                } else {
                    sendError(405, "Method Not Allowed", "Разрешены только GET и POST");
                }
            } catch (Exception e) {
                sendError(500, "Internal Server Error", "Ошибка сервера");
            }
        }
    }

    static void handlePost(String body, long start) {
        String x = null, y = null;
        ArrayList<String> rs = new ArrayList<>();

        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length != 2) continue;
            String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8).trim().replace(',', '.');
            if (kv[0].equals("action") && value.equals("clear")) {
                history.clear();
                sendHistory(start);
                return;
            }
            if (kv[0].equals("x")) x = value;
            if (kv[0].equals("y")) y = value;
            if (kv[0].equals("r")) rs.add(value);
        }

        // валид
        String error = checkNumber("X", x, -5, 5);
        if (error == null) error = checkNumber("Y", y, -3, 5);
        if (error == null) error = checkR(rs);
        if (error != null) {
            sendError(400, "Bad Request", error);
            return;
        }

        String now = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        for (String r : rs) {
            boolean hit = isHit(new BigDecimal(x), new BigDecimal(y), new BigDecimal(r));
            long micros = (System.nanoTime() - start) / 1000;
            history.add("{\"x\":\"" + x + "\",\"y\":\"" + y + "\",\"r\":\"" + r + "\",\"hit\":" + hit
                    + ",\"time\":\"" + now + "\",\"micros\":" + micros + "}");
        }
        sendHistory(start);
    }


    static String checkNumber(String name, String s, int min, int max) {
        if (s == null || !s.matches("-?\\d+(\\.\\d+)?")) {
            return name + " должен быть числом";
        }
        BigDecimal v = new BigDecimal(s);
        if (v.compareTo(BigDecimal.valueOf(min)) <= 0 || v.compareTo(BigDecimal.valueOf(max)) >= 0) {
            return name + " должен быть в интервале (" + min + " ... " + max + ")";
        }
        return null;
    }

    static String checkR(ArrayList<String> rs) {
        if (rs.isEmpty()) return "Не выбрано значение R";
        for (int i = 0; i < rs.size(); i++) {
            if (!rs.get(i).matches("[1-5]")) return "R должен быть одним из 1, 2, 3, 4, 5";
            if (rs.indexOf(rs.get(i)) != i) return "Значения R не должны повторяться";
        }
        return null;
    }

    static boolean isHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        BigDecimal two = BigDecimal.valueOf(2), four = BigDecimal.valueOf(4);
        boolean rectangle = x.signum() >= 0 && y.signum() >= 0
                && x.multiply(two).compareTo(r) <= 0 && y.compareTo(r) <= 0;
        boolean circle = x.signum() <= 0 && y.signum() >= 0
                && x.multiply(x).add(y.multiply(y)).multiply(four).compareTo(r.multiply(r)) <= 0;
        boolean triangle = x.signum() >= 0 && y.signum() <= 0
                && x.subtract(y).compareTo(r) <= 0;
        return rectangle || circle || triangle;
    }

    static String readBody() throws Exception {
        String lenStr = FCGIInterface.request.params.getProperty("CONTENT_LENGTH");
        int len = (lenStr == null || lenStr.isEmpty()) ? 0 : Integer.parseInt(lenStr);
        InputStream in = FCGIInterface.request.inStream;
        byte[] buf = new byte[len];
        int read = 0;
        while (read < len) {
            int n = in.read(buf, read, len - read);
            if (n <= 0) break;
            read += n;
        }
        return new String(buf, 0, read, StandardCharsets.UTF_8);
    }

    static void sendHistory(long start) {
        String now = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        long micros = (System.nanoTime() - start) / 1000;
        String json = "{\"history\":[" + String.join(",", history) + "],"
                + "\"currentTime\":\"" + now + "\",\"micros\":" + micros + "}";
        send(200, "OK", json);
    }

    static void sendError(int code, String status, String message) {
        send(code, status, "{\"error\":\"" + message + "\"}");
    }


    static void send(int code, String status, String json) {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 " + code + " " + status + "\r\n"
                + "Content-Type: application/json; charset=utf-8\r\n"
                + "Content-Length: " + body.length + "\r\n\r\n";
        System.out.print(headers);
        System.out.write(body, 0, body.length);
        System.out.flush();
    }
}
