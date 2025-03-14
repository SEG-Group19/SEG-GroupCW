package com.adauction.group19.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Record ExportData
 * 
 * @param header The header of the data. (e.g. "Impressions", "Clicks",
 *               "Conversions", "Cost", "CTR", "CPC", "CPA", "CPM", "Bounce
 *               Rate") "Date" is not included in the header.
 * @param dates  The dates of the data.
 * @param rows   The data of the data.
 */
public record ExportData(List<String> header, List<String> dates, List<List<Number>> rows) {
	public String toCSV() {
		if (dates.size() != rows.size()) {
			throw new IllegalArgumentException("Dates and rows must have the same size");
		}
        String headerLine = "Date," + String.join(",", header);
        String dataLines = IntStream.range(0, dates.size())
               .mapToObj(i -> dates.get(i) + "," + rows.get(i).stream()
                       .map(Object::toString)
                       .collect(Collectors.joining(",")))
               .collect(Collectors.joining("\n"));

        return headerLine + "\n" + dataLines;
	}
}
