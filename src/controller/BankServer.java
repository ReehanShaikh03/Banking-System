package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import service.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BankServer {
    private static final int PORT = 8080;
    private static Bank bank = new Bank();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/accounts/savings", new CreateSavingsHandler());
        server.createContext("/api/accounts/current", new CreateCurrentHandler());
        server.createContext("/api/accounts/deposit", new DepositHandler());
        server.createContext("/api/accounts/withdraw", new WithdrawHandler());
        server.createContext("/api/accounts/transfer", new TransferHandler());
        server.createContext("/api/accounts/statement", new StatementHandler());
        server.createContext("/api/accounts", new AccountHandler());

        server.setExecutor(null); // Default executor
        System.out.println("==================================================");
        System.out.println("[BANK] REST API Server started on http://localhost:" + PORT);
        System.out.println("==================================================");
        server.start();
    }

    private static void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static boolean checkCorsAndOptions(HttpExchange exchange) throws IOException {
        handleCors(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return true;
        }
        return false;
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.trim().isEmpty()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private static Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            body.append(line);
        }
        String bodyStr = body.toString().trim();
        Map<String, String> params = new HashMap<>();

        if (bodyStr.startsWith("{") && bodyStr.endsWith("}")) {
            // Simple JSON parser for basic key-value strings and numbers
            String content = bodyStr.substring(1, bodyStr.length() - 1);
            String[] tokens = content.split(",");
            for (String token : tokens) {
                String[] kv = token.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].trim().replaceAll("^\"|\"$", "");
                    String v = kv[1].trim().replaceAll("^\"|\"$", "");
                    params.put(k, v);
                }
            }
        } else if (bodyStr.contains("=")) {
            params.putAll(parseQueryParams(bodyStr));
        }

        return params;
    }

    private static Map<String, String> getMergedParams(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
        Map<String, String> bodyParams = parseRequestBody(exchange);
        Map<String, String> merged = new HashMap<>(queryParams);
        merged.putAll(bodyParams);
        return merged;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", responseText.startsWith("{") ? "application/json" : "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String getParam(Map<String, String> params, String... keys) {
        for (String key : keys) {
            if (params.containsKey(key) && params.get(key) != null && !params.get(key).isEmpty()) {
                return params.get(key);
            }
        }
        return null;
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            sendResponse(exchange, 200, "{\"status\":\"UP\",\"message\":\"Banking API is running\"}");
        }
    }

    static class CreateSavingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            try {
                Map<String, String> params = getMergedParams(exchange);
                String accNum = getParam(params, "accNum", "accountNumber");
                String name = getParam(params, "name", "holderName");
                String balStr = getParam(params, "balance", "initialBalance");

                if (accNum == null || name == null || balStr == null) {
                    sendResponse(exchange, 400, "Error: Missing required parameters (accNum, name, balance).");
                    return;
                }

                double balance = Double.parseDouble(balStr);
                String result = bank.createSavingsAccount(accNum, name, balance);
                int code = result.startsWith("Error") ? 400 : 200;
                sendResponse(exchange, code, result);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Error: Invalid balance format.");
            } catch (Exception e) {
                sendResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }

    static class CreateCurrentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            try {
                Map<String, String> params = getMergedParams(exchange);
                String accNum = getParam(params, "accNum", "accountNumber");
                String name = getParam(params, "name", "holderName");
                String balStr = getParam(params, "balance", "initialBalance");

                if (accNum == null || name == null || balStr == null) {
                    sendResponse(exchange, 400, "Error: Missing required parameters (accNum, name, balance).");
                    return;
                }

                double balance = Double.parseDouble(balStr);
                String result = bank.createCurrentAccount(accNum, name, balance);
                int code = result.startsWith("Error") ? 400 : 200;
                sendResponse(exchange, code, result);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Error: Invalid balance format.");
            } catch (Exception e) {
                sendResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }

    static class DepositHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            try {
                Map<String, String> params = getMergedParams(exchange);
                String accNum = getParam(params, "accNum", "accountNumber");
                String amtStr = getParam(params, "amount");

                if (accNum == null || amtStr == null) {
                    sendResponse(exchange, 400, "Error: Missing accNum or amount.");
                    return;
                }

                double amount = Double.parseDouble(amtStr);
                String result = bank.deposit(accNum, amount);
                sendResponse(exchange, 200, result);
            } catch (InvalidAmountException e) {
                sendResponse(exchange, 400, e.getMessage());
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Error: Invalid amount format.");
            } catch (Exception e) {
                sendResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }

    static class WithdrawHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            try {
                Map<String, String> params = getMergedParams(exchange);
                String accNum = getParam(params, "accNum", "accountNumber");
                String amtStr = getParam(params, "amount");

                if (accNum == null || amtStr == null) {
                    sendResponse(exchange, 400, "Error: Missing accNum or amount.");
                    return;
                }

                double amount = Double.parseDouble(amtStr);
                String result = bank.withdraw(accNum, amount);
                sendResponse(exchange, 200, result);
            } catch (InvalidAmountException | InsufficientFundsException e) {
                sendResponse(exchange, 400, e.getMessage());
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Error: Invalid amount format.");
            } catch (Exception e) {
                sendResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }

    static class TransferHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            try {
                Map<String, String> params = getMergedParams(exchange);
                String fromAcc = getParam(params, "fromAcc", "fromAccNum", "fromAccount");
                String toAcc = getParam(params, "toAcc", "toAccNum", "toAccount");
                String amtStr = getParam(params, "amount");

                if (fromAcc == null || toAcc == null || amtStr == null) {
                    sendResponse(exchange, 400, "Error: Missing fromAcc, toAcc, or amount.");
                    return;
                }

                double amount = Double.parseDouble(amtStr);
                String result = bank.transfer(fromAcc, toAcc, amount);
                sendResponse(exchange, 200, result);
            } catch (InvalidAmountException | InsufficientFundsException e) {
                sendResponse(exchange, 400, e.getMessage());
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Error: Invalid amount format.");
            } catch (Exception e) {
                sendResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }

    static class StatementHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            try {
                Map<String, String> params = getMergedParams(exchange);
                String accNum = getParam(params, "accNum", "accountNumber");

                if (accNum == null) {
                    sendResponse(exchange, 400, "Error: Missing accNum parameter.");
                    return;
                }

                Account account = bank.getAccount(accNum);
                if (account != null) {
                    String json = String.format("{\"accountNumber\":\"%s\",\"holderName\":\"%s\",\"balance\":%.2f}",
                            account.getAccountNumber(), account.getHolderName(), account.getBalance());
                    sendResponse(exchange, 200, json);
                } else {
                    sendResponse(exchange, 404, "Error: Account " + accNum + " not found.");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }

    static class AccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (checkCorsAndOptions(exchange)) return;
            StatementHandler handler = new StatementHandler();
            handler.handle(exchange);
        }
    }
}
