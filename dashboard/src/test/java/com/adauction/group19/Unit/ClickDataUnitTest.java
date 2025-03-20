package com.adauction.group19.Unit;

import com.adauction.group19.model.ClickData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ClickDataUnitTest {

    private LocalDateTime testDate;
    private String testId;
    private double testClickCost;
    private ClickData clickData;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.of(2025, 3, 19, 10, 0, 0);
        testId = "test123";
        testClickCost = 0.5;
        clickData = new ClickData(testDate, testId, testClickCost);
    }

    @Test
    void testConstructor() {
        assertNotNull(clickData, "ClickData object should not be null");
    }

    @Test
    void testGetDate() {
        assertEquals(testDate, clickData.getDate(), "getDate() should return the correct date");
    }

    @Test
    void testGetId() {
        assertEquals(testId, clickData.getId(), "getId() should return the correct ID");
    }

    @Test
    void testGetClickCost() {
        assertEquals(testClickCost, clickData.getClickCost(), "getClickCost() should return the correct cost");
    }

    @Test
    void testSetDate() {
        LocalDateTime newDate = LocalDateTime.of(2025, 3, 20, 11, 0, 0);
        clickData.setDate(newDate);
        assertEquals(newDate, clickData.getDate(), "setDate() should update the date correctly");
    }

    @Test
    void testSetId() {
        String newId = "new123";
        clickData.setId(newId);
        assertEquals(newId, clickData.getId(), "setId() should update the ID correctly");
    }

    @Test
    void testSetClickCost() {
        double newCost = 0.75;
        clickData.setClickCost(newCost);
        assertEquals(newCost, clickData.getClickCost(), "setClickCost() should update the cost correctly");
    }

    @Test
    void testSetClickCostWithZero() {
        clickData.setClickCost(0.0);
        assertEquals(0.0, clickData.getClickCost(), "setClickCost() should handle zero cost correctly");
    }

    @Test
    void testSetClickCostWithNegative() {
        clickData.setClickCost(-0.5);
        assertEquals(-0.5, clickData.getClickCost(), "setClickCost() should handle negative cost correctly");
    }

    @Test
    void testSetIdWithNull() {
        clickData.setId(null);
        assertNull(clickData.getId(), "setId() should handle null ID correctly");
    }

    @Test
    void testSetDateWithNull() {
        clickData.setDate(null);
        assertNull(clickData.getDate(), "setDate() should handle null date correctly");
    }
} 