package fr.ela.aoc2022;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D13 extends AoC {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void run() {
        //System.out.println("Test part one : " + partOne(readInput(getTestInputPath())));
        System.out.println("Real part one : " + partOne(readInput(getInputPath())));
    }

    public record Pair(int index, JsonNode left, JsonNode right) {
    }

    public List<Pair> readInput(Path path) {
        List<List<String>> list = splitOnEmptyLines(path);
        return IntStream.range(0, list.size()).mapToObj(i -> new Pair(i + 1, parse(list.get(i).get(0)), parse(list.get(i).get(1)))).toList();
    }

    public int partOne(List<Pair> pairs) {
        pairs.stream()
                .map(p -> p.left+"\n"+p.right+"\n"+(PACKET_COMPARATOR.compare(p.left, p.right) < 0 ? "OK":"KO"))
                .forEach(System.out::println);

        return pairs.stream()
                .filter(p -> PACKET_COMPARATOR.compare(p.left, p.right) < 0)
                .mapToInt( p -> p.index)
                .sum();
    }

    /*

    If both values are integers, the lower integer should come first.
    If the left integer is lower than the right integer, the inputs are in the right order.
    If the left integer is higher than the right integer, the inputs are not in the right order.
    Otherwise, the inputs are the same integer; continue checking the next part of the input.

    If both values are lists, compare the first value of each list, then the second value, and so on.
    If the left list runs out of items first, the inputs are in the right order.
    If the right list runs out of items first, the inputs are not in the right order.
    If the lists are the same length and no comparison makes a decision about the order, continue checking the next part of the input.

    If exactly one value is an integer, convert the integer to a list which contains that integer as its only value,
    then retry the comparison.
    For example, if comparing [0,0,0] and 2, convert the right value to [2] (a list containing 2);
    the result is then found by instead comparing [0,0,0] and [2].
     */

    public Comparator<JsonNode> PACKET_COMPARATOR = new Comparator<>() {

        @Override
        public int compare(JsonNode one, JsonNode other) {
            try {
                if (one.isNumber()) {
                    if (other.isNumber()) {
                        System.out.println("Comparing ints : " + one.numberValue() + "," + other.numberValue());
                        return one.asInt() - other.asInt();
                    } else {
                        return compareArrays(asArray(one), other);
                    }
                }
                if (other.isNumber()) {
                    return compareArrays(one, asArray(other));
                }
                return compareArrays(one, other);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        private JsonNode asArray(JsonNode other) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            arrayNode.add(other);
            return arrayNode;
        }

        private int compareArrays(JsonNode one, JsonNode other) throws JsonProcessingException {
            var it1 = one.elements();
            var it2 = other.elements();
            while (it1.hasNext() && it2.hasNext()) {
                int comparison = compare(it1.next(), it2.next());
                if (comparison != 0) {
                    System.out.println("Comparing arrays " + objectMapper.writeValueAsString(one) + ", " + objectMapper.writer().writeValueAsString(other) + " : " + comparison);
                    return comparison;
                }
            }
            int comparison = it1.hasNext() ? 1 : it2.hasNext() ? -1 : 0;
            System.out.println("Comparing arrays " + objectMapper.writeValueAsString(one) + ", " + objectMapper.writer().writeValueAsString(other) + " : " + comparison);
            return comparison;
        }
    };


    public JsonNode parse(String line) {
        try {
            return objectMapper.reader().readTree(line);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
