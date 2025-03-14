package com.adauction.group19.model;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Record ExportData
 * 
 * @param header The header of the data. (e.g. "Impressions", "Clicks",
 *               "Conversions", "Cost", "CTR", "CPC", "CPA", "CPM", "Bounce
 *               Rate") "Date" is not included in the header.
 * @param dateToValue A map of dates to the values of the data.
 */
public record ExportData(List<String> header, TreeMap<String, List<Number>> dateToValue) {
	public String toCSV() {
        String headerLine = "Date," + String.join(",", header);
		String dataLines = dateToValue.entrySet().stream()
			.map(entry -> entry.getKey() + "," + entry.getValue().stream()
				.map(Object::toString)
				.collect(Collectors.joining(",")))
			.collect(Collectors.joining("\n"));

        return headerLine + "\n" + dataLines;
	}

	public String toJSON() {
        return "[" + dateToValue.entrySet().stream()
               .map(entry -> {
                    String date = entry.getKey();
                    String dataJson = IntStream.range(0, header.size())
                           .mapToObj(j -> "\"" + header.get(j) + "\": " + entry.getValue().get(j))
                           .collect(Collectors.joining(","));
                    return "{\"date\": \"" + date + "\", \"data\": {" + dataJson + "}}";
                })
               .collect(Collectors.joining(",")) + "]";
	}
}
