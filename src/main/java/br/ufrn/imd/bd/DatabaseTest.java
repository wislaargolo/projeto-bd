package br.ufrn.imd.bd;

import br.ufrn.imd.bd.connection.DatabaseConfig;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest
public class DatabaseTest {

    @Test
    public void testDatabaseConnection() {
        Connection connection = null;
        try {
            // Estabelece a conexão com o banco de dados
            connection = DatabaseConfig.getConnection();
            // Verifica se a conexão não é nula
            assertNotNull(connection);
            // Se chegou até aqui, a conexão foi estabelecida com sucesso
            System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
        } catch (SQLException e) {
            // Se houver uma exceção ao tentar estabelecer a conexão, imprima o stack trace
            e.printStackTrace();
        } finally {
            // Fecha a conexão, se estiver aberta
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Se houver uma exceção ao fechar a conexão, imprima o stack trace
                    e.printStackTrace();
                }
            }
        }
    }
}
