package dev.hxrry.hxgui.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotPattern {
    
    private final Map<Integer, Character> slotMap = new HashMap<>();
    private final Map<Character, Integer> firstOccurrence = new HashMap<>();
    private final int rows;
    
    private SlotPattern(int rows) {
        this.rows = rows;
    }
    
    // create from visual pattern
    public static SlotPattern fromVisual(String... lines) {
        int rows = lines.length;
        SlotPattern pattern = new SlotPattern(rows);
        
        int slot = 0;
        for (String line : lines) {
            // remove spaces for easier typing
            line = line.replace(" ", "");
            
            // pad or truncate to 9 chars
            while (line.length() < 9) {
                line += "-";
            }
            if (line.length() > 9) {
                line = line.substring(0, 9);
            }
            
            // parse each character
            for (int i = 0; i < 9; i++) {
                char c = line.charAt(i);
                
                // skip empty slots
                if (c == '-' || c == '_' || c == ' ') {
                    slot++;
                    continue;
                }
                
                // map the character to slot
                pattern.slotMap.put(slot, c);
                
                // track first occurrence for centering items
                pattern.firstOccurrence.putIfAbsent(c, slot);
                
                slot++;
            }
        }
        
        return pattern;
    }
    
    // create from explicit slot mapping
    public static SlotPattern fromSlots(Map<Integer, Character> slots) {
        // find max slot to determine rows
        int maxSlot = slots.keySet().stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(8);
        
        int rows = (maxSlot / 9) + 1;
        SlotPattern pattern = new SlotPattern(rows);
        
        // copy the mappings
        pattern.slotMap.putAll(slots);
        
        // find first occurrences
        for (Map.Entry<Integer, Character> entry : slots.entrySet()) {
            pattern.firstOccurrence.putIfAbsent(entry.getValue(), entry.getKey());
        }
        
        return pattern;
    }
    
    // create from regions
    public static SlotPattern fromRegions(Map<String, List<Integer>> regions) {
        // find max slot to determine rows
        int maxSlot = regions.values().stream()
            .flatMap(List::stream)
            .mapToInt(Integer::intValue)
            .max()
            .orElse(8);
        
        int rows = (maxSlot / 9) + 1;
        SlotPattern pattern = new SlotPattern(rows);
        
        // assign characters to regions
        char regionChar = 'A';
        for (Map.Entry<String, List<Integer>> entry : regions.entrySet()) {
            char c = regionChar++;
            
            for (int slot : entry.getValue()) {
                pattern.slotMap.put(slot, c);
                pattern.firstOccurrence.putIfAbsent(c, slot);
            }
        }
        
        return pattern;
    }
    
    // parse range notation like "0-8" or "10-16,19-25"
    public static SlotPattern fromRanges(String ranges) {
        Map<Integer, Character> slots = new HashMap<>();
        char currentChar = 'A';
        
        String[] parts = ranges.split(",");
        for (String part : parts) {
            part = part.trim();
            
            if (part.contains("-")) {
                // range like "0-8"
                String[] range = part.split("-");
                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());
                
                for (int i = start; i <= end; i++) {
                    slots.put(i, currentChar);
                }
                currentChar++;
                
            } else {
                // single slot
                int slot = Integer.parseInt(part);
                slots.put(slot, currentChar);
                currentChar++;
            }
        }
        
        return fromSlots(slots);
    }
    
    // get the slot mapping
    public Map<Integer, Character> getSlotMap() {
        return new HashMap<>(slotMap);
    }
    
    // get all slots for a character
    public List<Integer> getSlotsFor(char c) {
        return slotMap.entrySet().stream()
            .filter(entry -> entry.getValue() == c)
            .map(Map.Entry::getKey)
            .toList();
    }
    
    // get character at slot
    public Character getCharAt(int slot) {
        return slotMap.get(slot);
    }
    
    // check if slot has a character
    public boolean hasCharAt(int slot) {
        return slotMap.containsKey(slot);
    }
    
    // get first slot for character
    public Integer getFirstSlotFor(char c) {
        return firstOccurrence.get(c);
    }
    
    // get all unique characters
    public String getUniqueChars() {
        return firstOccurrence.keySet().stream()
            .map(String::valueOf)
            .reduce("", String::concat);
    }
    
    // get rows
    public int getRows() {
        return rows;
    }
    
    // merge with another pattern
    public SlotPattern merge(SlotPattern other) {
        SlotPattern merged = new SlotPattern(Math.max(this.rows, other.rows));
        merged.slotMap.putAll(this.slotMap);
        merged.slotMap.putAll(other.slotMap); // other overwrites conflicts
        
        // recalculate first occurrences
        merged.firstOccurrence.clear();
        for (Map.Entry<Integer, Character> entry : merged.slotMap.entrySet()) {
            merged.firstOccurrence.putIfAbsent(entry.getValue(), entry.getKey());
        }
        
        return merged;
    }
    
    // apply a border pattern
    public SlotPattern withBorder(char borderChar) {
        SlotPattern bordered = new SlotPattern(rows);
        bordered.slotMap.putAll(this.slotMap);
        
        // add border slots
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;
                
                // check if border position
                if (row == 0 || row == rows - 1 || col == 0 || col == 8) {
                    // only add if not already occupied
                    bordered.slotMap.putIfAbsent(slot, borderChar);
                }
            }
        }
        
        // recalculate first occurrences
        bordered.firstOccurrence.clear();
        for (Map.Entry<Integer, Character> entry : bordered.slotMap.entrySet()) {
            bordered.firstOccurrence.putIfAbsent(entry.getValue(), entry.getKey());
        }
        
        return bordered;
    }
    
    // debug visualization
    public String visualize() {
        StringBuilder result = new StringBuilder();
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;
                Character c = slotMap.get(slot);
                result.append(c != null ? c : '-');
            }
            result.append("\n");
        }
        
        return result.toString();
    }
    
    // create common patterns
    
    public static SlotPattern border() {
        return fromVisual(
            "BBBBBBBBB",
            "B-------B",
            "B-------B",
            "B-------B",
            "B-------B",
            "BBBBBBBBB"
        );
    }
    
    public static SlotPattern corners() {
        return fromVisual(
            "C-------C",
            "---------",
            "---------",
            "---------",
            "---------",
            "C-------C"
        );
    }
    
    public static SlotPattern checkerboard() {
        return fromVisual(
            "ABABABABAB",
            "BABABABABA",
            "ABABABABAB",
            "BABABABABA",
            "ABABABABAB",
            "BABABABABA"
        );
    }
}