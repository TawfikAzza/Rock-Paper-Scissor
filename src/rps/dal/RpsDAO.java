package rps.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import rps.bll.game.Result;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RpsDAO {
    ConnectionManager cm;
    public RpsDAO() {
        try {
            cm = new ConnectionManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,Integer> getAllMoves() {
        HashMap<String,Integer> mapMoves = new HashMap<>();
        try(Connection con = cm.getConnection()){
            String sqlGETALL = "SELECT * FROM playRecord";
            PreparedStatement pstmt = con.prepareStatement(sqlGETALL);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                mapMoves.put(rs.getString("move"),rs.getInt("occurence"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return mapMoves;
    }

    public void addMoves(HashMap<String,Integer> mapMoves) {
        try(Connection con = cm.getConnection()){
            String sqlDELALL = "DELETE FROM playRecord";
            PreparedStatement pstmt = con.prepareStatement(sqlDELALL);

            pstmt.execute();

            String sqlInsert = "INSERT INTO playRecord VALUES (?,?)";
            PreparedStatement pstmtInsert = con.prepareStatement(sqlInsert);
            for (Map.Entry entry:mapMoves.entrySet()) {
                String move = (String) entry.getKey();
                Integer occurence = (Integer) entry.getValue();
                pstmtInsert.setString(1,move);
                pstmtInsert.setInt(2,occurence);
                pstmtInsert.addBatch();
            }
            pstmtInsert.executeBatch();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
