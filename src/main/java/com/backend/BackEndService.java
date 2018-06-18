package java.com.backend;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static spark.Spark.*;
import static java.com.backend.JsonUtil.json;

public class BackEndService {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    @Contract(pure = true)
    private static String[] getDBDetails() {
        try {
            String[] dbDetails = new String[4];
            dbDetails[0] = "jdbc:mysql://db:3306";
            dbDetails[1] = "root";
            dbDetails[2] = "root";
            dbDetails[3] = "jdbc:mysql://db:3306/DummyTable";


            return dbDetails;
        }
        catch (Exception exception){
            return new String[] {"", "", "", ""};
        }
    }

    private static ArrayList<TeamInfo> getAllTeamNames(){

        ArrayList<TeamInfo> teamInfos = new ArrayList<>();

        String[] dbDetails = getDBDetails();
        String queryStatement = "SELECT * from DummyTableResults";

        try (Connection databaseConnection = DriverManager.getConnection(dbDetails[3], dbDetails[1], dbDetails[2]);
             Statement stmt = databaseConnection.createStatement();
             ResultSet resultSet = stmt.executeQuery(queryStatement);
             )
        {
            Class.forName(JDBC_DRIVER);

            while (resultSet.next()) {
                String teamName = resultSet.getString("teamName");
                String rawdata = resultSet.getString("rawdata");

                TeamInfo teamInfo = new TeamInfo(teamName, rawdata);

                teamInfos.add(teamInfo);
            }

            return teamInfos;
        }
        catch (Exception exception){
            return teamInfos;
        }

    }

    @NotNull
    private static String insertTeamName(String teamName, String rawData){

        createDatabaseIfItDoesNotExists();
        String[] dbDetails = getDBDetails();

        try (Connection conn = DriverManager.getConnection(dbDetails[3], dbDetails[1], dbDetails[2]);
             Statement stmt = conn.createStatement();
             ) {
            Class.forName(JDBC_DRIVER);


            String sql = String.format("REPLACE INTO DummyTableResults VALUES ('%s','%s')", teamName, rawData);

            int insertedRecord = stmt.executeUpdate(sql);

            if (insertedRecord > 0) {
                return "Successfully inserted record";
            } else {
                return "Record not inserted";
            }
        }
        catch (Exception exception){
            return "Error Code: " + exception.toString();
        }


    }


    private static String createDatabaseIfItDoesNotExists(){
        String[] dbDetails = getDBDetails();

        try (Connection conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
             Statement statement = conn.createStatement();
             ) {
            Class.forName(JDBC_DRIVER);

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS DummyTable;");
            createTableIfItDoesNotExists();
            return "The table and database have been successfully created.";
        }
        catch (Exception exception){
            return exception.getMessage();
        }
    }

    private static String createTableIfItDoesNotExists(){
        String[] dbDetails = getDBDetails();

        try (Connection conn = DriverManager.getConnection(dbDetails[3], dbDetails[1], dbDetails[2]);
             Statement statement = conn.createStatement();
        ) {
            Class.forName(JDBC_DRIVER);
            String sqlCreate = "CREATE TABLE IF NOT EXISTS DummyTableResults"
                    + "  (teamName           VARCHAR(150),"
                    + "   rawdata            longtext,"
                    + "   UNIQUE KEY my_unique_key (teamName))";

            statement.execute(sqlCreate);
            return "Created the table";
        }
        catch (Exception exception){
            return exception.getMessage();
        }
    }

    public static void main(String[] args) {

        port(8081);

        get("/configure", new Route() {
            public Object handle(Request req, Response res) {
                return createDatabaseIfItDoesNotExists();
            }
        }, json());

        post("/saveTeamData", new Route() {
            public Object handle(Request request, Response response) throws Exception {

                String teamName = request.queryParams("teamName");
                String rawData = request.queryParams("rawData");

                return insertTeamName(teamName, rawData);
            }}, json());

        get("/allTeams", new Route() {
            public Object handle(Request req, Response res) {
                return getAllTeamNames();
            }
        }, json());

        options("/*",
                new Route() {
                    public Object handle(Request request, Response response) throws Exception {

                        String accessControlRequestHeaders = request
                                .headers("Access-Control-Request-Headers");
                        if (accessControlRequestHeaders != null) {
                            response.header("Access-Control-Allow-Headers",
                                    accessControlRequestHeaders);
                        }

                        String accessControlRequestMethod = request
                                .headers("Access-Control-Request-Method");
                        if (accessControlRequestMethod != null) {
                            response.header("Access-Control-Allow-Methods",
                                    accessControlRequestMethod);
                        }

                        return "OK";
                    }
                });

        before(new Filter() {
            public void handle(Request request, Response response) throws Exception {
                response.header("Access-Control-Allow-Origin", "*");
            }
        });
    }



}
