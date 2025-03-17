package com.adauction.group19.service;

import com.adauction.group19.model.ClickData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClickDistributionService {
    
    public Map<Double, Integer> calculateClickCostDistribution(String filePath, int numBins) throws IOException {
        List<ClickData> clickData = loadClickData(filePath);
        return createHistogramData(clickData, numBins);
    }

    private List<ClickData> loadClickData(String filePath) throws IOException {
        List<ClickData> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            br.readLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3) {
                    LocalDateTime date = LocalDateTime.parse(values[0], formatter);
                    String id = values[1];
                    double cost = Double.parseDouble(values[2]);
                    data.add(new ClickData(date, id, cost));
                }
            }
        }
        return data;
    }

    private Map<Double, Integer> createHistogramData(List<ClickData> data, int numBins) {
        // Find min and max click costs
        double minCost = data.stream()
                .mapToDouble(ClickData::getClickCost)
                .min()
                .orElse(0.0);
        double maxCost = data.stream()
                .mapToDouble(ClickData::getClickCost)
                .max()
                .orElse(0.0);

        // Calculate bin width
        double binWidth = (maxCost - minCost) / numBins;

        // Create histogram using TreeMap to maintain order
        Map<Double, Integer> histogram = new TreeMap<>();
        
        // Initialize bins
        for (int i = 0; i < numBins; i++) {
            double binStart = minCost + (i * binWidth);
            histogram.put(binStart, 0);
        }

        // Fill the bins
        for (ClickData click : data) {
            double cost = click.getClickCost();
            double bin = minCost + (Math.floor((cost - minCost) / binWidth) * binWidth);
            histogram.put(bin, histogram.getOrDefault(bin, 0) + 1);
        }

        return histogram;
    }
} 