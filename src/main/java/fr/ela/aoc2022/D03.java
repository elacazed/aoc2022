package fr.ela.aoc2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class D03 extends AoC {

    record Item(char c) {
        public int priority() {
            return 1 + (Character.isLowerCase(c) ? c - 'a' : c - 'A' + 26);
        }
    }

    record Compartment(List<Item> items) {
        int commonElements(Compartment others) {
            Set<Item> intersection = new HashSet<>(items);
            intersection.retainAll(others.items);
            return intersection.stream()
                    .mapToInt(Item::priority).sum();
        }
    }

    record RuckSack(Compartment first, Compartment second) {

        int score() {
            return first.commonElements(second);
        }

        Set<Item> items() {
            Set<Item> items = new HashSet<>(first.items);
            items.addAll(second.items);
            return items;
        }
    }

    record Group(RuckSack[] ruckSacks) {
        int badgePriority() {
            Set<Item> commonItems = ruckSacks[0].items();
            for (int i = 1; i < ruckSacks.length; i++) {
                commonItems.retainAll(ruckSacks[i].items());
            }
            return commonItems.iterator().next().priority();
        }
    }

    static Compartment compartmentOf(String value) {
        List<Item> items = new ArrayList<>();
        for (char c : value.toCharArray()) {
            items.add(new Item(c));
        }
        return new Compartment(items);
    }

    static RuckSack ruckSackOf(String line) {
        int length = line.length() / 2;
        return new RuckSack(compartmentOf(line.substring(0, length)), compartmentOf(line.substring(length)));
    }

    static class Groups {
        List<Group> groups = new ArrayList<>();
        RuckSack[] ruckSacks = new RuckSack[3];
        int nb;
        public void add(RuckSack ruckSack) {
            ruckSacks[nb] = ruckSack;
            if (nb == 2) {
                groups.add(new Group(ruckSacks));
                ruckSacks = new RuckSack[3];
                nb = 0;
            } else {
                nb++;
            }
        }

        public void combine(Groups other) {
            groups.addAll(other.groups);
        }
    }


    @Override
    public void run() {
        System.out.println("Test Score Part 1 : " + stream(getTestInputPath(), D03::ruckSackOf).mapToInt(RuckSack::score).sum());
        System.out.println("Real Score Part 1 : " + stream(getInputPath(), D03::ruckSackOf).mapToInt(RuckSack::score).sum());

        System.out.println("Test Score Part 2 : " + stream(getTestInputPath(), D03::ruckSackOf).collect(Groups::new, Groups::add, Groups::combine).groups.stream().mapToInt(Group::badgePriority).sum());
        System.out.println("Real Score Part 2 : " + stream(getInputPath(), D03::ruckSackOf).collect(Groups::new, Groups::add, Groups::combine).groups.stream().mapToInt(Group::badgePriority).sum());
    }


}
