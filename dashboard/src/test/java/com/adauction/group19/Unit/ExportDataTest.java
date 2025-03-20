package com.adauction.group19.Unit;

import com.adauction.group19.model.ExportData;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.TreeMap;
import static org.junit.jupiter.api.Assertions.*;

class ExportDataTest {

    @Test
    void testToCSV_NormalCase() {
        TreeMap<String, List<Number>> data = new TreeMap<>();
        data.put("2023-10-01", List.of(100, 200));
        data.put("2023-10-02", List.of(150, 250));
        
        ExportData exportData = new ExportData(List.of("Impressions", "Clicks"), data);
        
        String expected = "Date,Impressions,Clicks\n"
                        + "2023-10-01,100,200\n"
                        + "2023-10-02,150,250";
        assertEquals(expected, exportData.toCSV());
    }

    @Test
    void testToCSV_EmptyData() {
        ExportData exportData = new ExportData(List.of(), new TreeMap<>());
        assertEquals("Date,\n", exportData.toCSV().trim() + "\n"); // When there is no data, there is only a header
    }

    @Test
    void testToJSON_NormalCase() {
        TreeMap<String, List<Number>> data = new TreeMap<>();
        data.put("2023-10-01", List.of(100, 200));
        
        ExportData exportData = new ExportData(List.of("Impressions", "Clicks"), data);
        
        String expected = "[{\"date\": \"2023-10-01\", \"data\": {\"Impressions\": 100,\"Clicks\": 200}}]";
        assertEquals(expected, exportData.toJSON());
    }

    @Test
    void testToJSON_MismatchedHeaderAndValues() {
        TreeMap<String, List<Number>> data = new TreeMap<>();
        data.put("2023-10-01", List.of(100)); // The number of values ​​is less than the header
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            ExportData exportData = new ExportData(List.of("Impressions", "Clicks"), data);
            exportData.toJSON(); // An exception is thrown when trying to access header.get(1)
        });
    }

    @Test
    void testRecordStructure() {
        TreeMap<String, List<Number>> data = new TreeMap<>();
        List<String> header = List.of("Test");
        ExportData exportData = new ExportData(header, data);
        
        assertEquals(header, exportData.header());
        assertEquals(data, exportData.dateToValue());
    }
}