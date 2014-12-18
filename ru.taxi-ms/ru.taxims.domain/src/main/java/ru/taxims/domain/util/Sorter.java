package ru.taxims.domain.util;

import ru.taxims.domain.datamodels.Feature;

import java.util.*;

/**
 * Created by Developer_DB on 08.09.14.
 */
public class Sorter
{
	public static Map sortByValues(Map map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
					.compareTo(((Map.Entry)(o2)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static Map sortFeatureBySequence(Map<Integer, Feature> map) {
		List list = new LinkedList<Map.Entry<Integer, Feature>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Feature>>() {
			public int compare(Map.Entry<Integer, Feature> e1, Map.Entry<Integer, Feature> e2) {
				Feature f1 = (Feature)e1.getValue();
				Feature f2 = (Feature)e2.getValue();
				return ((Integer)f1.getSequence()).compareTo(f2.getSequence());
			}
		});
		Map<Integer, Feature> sortedMap = new LinkedHashMap<Integer, Feature>();
		for (Iterator<Map.Entry<Integer, Feature>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Feature> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
