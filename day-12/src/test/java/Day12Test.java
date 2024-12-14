import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day12Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            //@AocInputMapping(input = "test.txt", expected = "5"),
            @AocInputMapping(input = "test1.txt", expected = "772"),
            @AocInputMapping(input = "test2.txt", expected = "1930"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/12/input", expected = "1319878")
    })
    void part1(Stream<String> input, String expected) {
        int[][] map = input.map(s -> s.codePoints().toArray()).toArray(int[][]::new);
        var claimed = new HashSet<Point>();
        var regions = new ArrayList<Region>();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (!claimed.contains(new Point(x, y))) {
                    regions.add(new Region(map, claimed, x, y));
                }
            }
        }


        assertEquals(Integer.parseInt(expected), regions.stream().mapToInt(Region::calculateCost).sum());
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "80"),
            @AocInputMapping(input = "test2.txt", expected = "1206"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/12/input", expected = "1319878")
    })
    void part2(Stream<String> input, String expected) {
        int[][] map = input.map(s -> s.codePoints().toArray()).toArray(int[][]::new);
        var claimed = new HashSet<Point>();
        var regions = new ArrayList<Region>();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (!claimed.contains(new Point(x, y))) {
                    regions.add(new Region(map, claimed, x, y));
                }
            }
        }

        assertEquals(Integer.parseInt(expected), regions.stream().mapToInt(Region::calculateOptimizedCost).sum());
    }

    record Point(int x, int y) {
        public Point move(Dir d) {
            return new Point(this.x + d.x, this.y + d.y);
        }
    }

    enum Dir {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);
        final int x;
        final int y;

        Dir(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class Region {
        final int plant;
        final Set<Point> points;
        final Set<Point> claimed;
        final int[][] map;

        Region(int[][] map, Set<Point> claimed, int x, int y) {
            this.plant = map[y][x];
            this.map = map;
            this.claimed = claimed;
            this.points = new HashSet<>();
            this.points.add(new Point(x, y));
            searchNeighburs(x, y);
        }

        private void searchNeighburs(int x, int y) {
            Arrays.stream(Dir.values()).forEach(d -> {
                var newX = d.x + x;
                var newY = d.y + y;

                if (newY < 0 || map.length <= newY) {
                    return;
                }
                if (newX < 0 || map[newY].length <= newX) {
                    return;
                }
                var newP = new Point(newX, newY);
                if (map[newY][newX] == plant && !claimed.contains(newP)) {
                    claimed.add(newP);
                    points.add(newP);
                    searchNeighburs(newX, newY);
                }
            });
        }

        private int calculateCost() {
            AtomicInteger fence = new AtomicInteger(0);
            for (var p : points) {
                Arrays.stream(Dir.values()).forEach(d -> {
                    var fenceX = d.x + p.x;
                    var fenceY = d.y + p.y;

                    if (!points.contains(new Point(fenceX, fenceY))) {
                        fence.incrementAndGet();
                    }
                });
            }
            return points.size() * fence.get();
        }


        private int calculateOptimizedCost() {
            var fencePoints = new ArrayList<Point>();
            for (var p : points) {
                Arrays.stream(Dir.values()).forEach(d -> {
                    var fenceX = d.x + p.x;
                    var fenceY = d.y + p.y;
                    var fence = new Point(fenceX, fenceY);

                    if (!points.contains(fence)) {
                        fencePoints.add(fence);
                    }
                });
            }
            var optimizedFences = new AtomicInteger(0);
            for (var p : points) {
                var neighbors = Arrays.stream(Dir.values()).map(d -> {
                    if (points.contains(p.move(d))) {
                        return p.move(d);
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).toList();
                if (neighbors.isEmpty()) {
                    optimizedFences.addAndGet(4);
                    continue;
                }

				// oposing have never corners
                if (points.contains(p.move(Dir.NORTH)) && points.contains(p.move(Dir.SOUTH))) {
                    continue;
                }
                if (points.contains(p.move(Dir.WEST)) && points.contains(p.move(Dir.EAST))) {
                    continue;
                }

                // NW
                if(!points.contains(p.move(Dir.NORTH).move(Dir.WEST)) &&
                        points.contains(p.move(Dir.NORTH)) &&
                        points.contains(p.move(Dir.WEST))){
                    optimizedFences.incrementAndGet();
                }

                if(!points.contains(p.move(Dir.NORTH).move(Dir.WEST)) &&
                        !points.contains(p.move(Dir.NORTH)) &&
                        !points.contains(p.move(Dir.WEST))){
                    optimizedFences.incrementAndGet();
                }
                if(points.contains(p.move(Dir.NORTH).move(Dir.WEST)) &&
                        !points.contains(p.move(Dir.NORTH)) &&
                        !points.contains(p.move(Dir.WEST))){
                    optimizedFences.incrementAndGet();
                }
                // NE
                if(!points.contains(p.move(Dir.NORTH).move(Dir.EAST)) &&
                        points.contains(p.move(Dir.NORTH)) &&
                        points.contains(p.move(Dir.EAST))){
                    optimizedFences.incrementAndGet();
                }
                if(!points.contains(p.move(Dir.NORTH).move(Dir.EAST)) &&
                        !points.contains(p.move(Dir.NORTH)) &&
                        !points.contains(p.move(Dir.EAST))){
                    optimizedFences.incrementAndGet();
                }
                if(points.contains(p.move(Dir.NORTH).move(Dir.EAST)) &&
                        !points.contains(p.move(Dir.NORTH)) &&
                        !points.contains(p.move(Dir.EAST))){
                    optimizedFences.incrementAndGet();
                }
                // SW
                if(!points.contains(p.move(Dir.SOUTH).move(Dir.WEST)) &&
                        points.contains(p.move(Dir.SOUTH)) &&
                        points.contains(p.move(Dir.WEST))){
                    optimizedFences.incrementAndGet();
                }
                if(!points.contains(p.move(Dir.SOUTH).move(Dir.WEST)) &&
                        !points.contains(p.move(Dir.SOUTH)) &&
                        !points.contains(p.move(Dir.WEST))){
                    optimizedFences.incrementAndGet();
                }
                if(points.contains(p.move(Dir.SOUTH).move(Dir.WEST)) &&
                        !points.contains(p.move(Dir.SOUTH)) &&
                        !points.contains(p.move(Dir.WEST))){
                    optimizedFences.incrementAndGet();
                }
                // SE
                if(!points.contains(p.move(Dir.SOUTH).move(Dir.EAST)) &&
                        points.contains(p.move(Dir.SOUTH)) &&
                        points.contains(p.move(Dir.EAST))){
                    optimizedFences.incrementAndGet();
                }
                if(!points.contains(p.move(Dir.SOUTH).move(Dir.EAST)) &&
                        !points.contains(p.move(Dir.SOUTH)) &&
                        !points.contains(p.move(Dir.EAST))){
                    optimizedFences.incrementAndGet();
                }
                if(points.contains(p.move(Dir.SOUTH).move(Dir.EAST)) &&
                        !points.contains(p.move(Dir.SOUTH)) &&
                        !points.contains(p.move(Dir.EAST))){
                    optimizedFences.incrementAndGet();
                }
            }

            return points.size() * optimizedFences.get();
        }
    }
}
