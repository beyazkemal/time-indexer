package com.kemalbeyaz;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

@QuarkusMain
public class StartTimeIndexing implements QuarkusApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartTimeIndexing.class);

    @Inject
    AgroalDataSource defaultDataSource;

    private static final String MODIFIED_DATE_COL_NAME = "modified";
    private static final String MODIFIED_TIME_COL_NAME = "modified_time";
    private static final String TIME_INDEX_COL_NAME = "time_index";

    @Override
    public int run(String... args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("No args!");
        }

        if (args[0] == null || args[0].isEmpty()) {
            throw new IllegalArgumentException("Empty args!");
        }

        String tableName = args[0];
        LOGGER.info("Selected Table: {}", tableName);

        int limit = 10000;
        if (args.length > 1 && args[1] != null && !args[1].isEmpty()) {
            limit = Integer.parseInt(args[1]);
        }
        LOGGER.info("Limit: {}", limit);

        long startTime = System.currentTimeMillis();

        Connection connection = defaultDataSource.getConnection();

        try (Statement findAllStatement = connection
                .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

            LOGGER.info("Starting...");
            ResultSet resultSet =
                    findAllStatement.executeQuery("select * from " + tableName + " where time_index = 0 limit " + limit);

            LOGGER.info("Started.");
            while (resultSet.next()) {
                Timestamp modified = resultSet.getTimestamp(MODIFIED_DATE_COL_NAME);
                long time = modified.getTime();
                long calculateTimeIndex = TimeIndexCalculator.calculateTimeIndexFor(time);

                resultSet.updateLong(MODIFIED_TIME_COL_NAME, time);
                resultSet.updateLong(TIME_INDEX_COL_NAME, calculateTimeIndex);
                resultSet.updateRow();
            }
            LOGGER.info("Finished.");
        }

        long endTime = System.currentTimeMillis();
        long estimatedTime = endTime - startTime;
        double seconds = (double) estimatedTime / 1000;
        LOGGER.info("The passing time: {} seconds", seconds);
        return 0;
    }
}
