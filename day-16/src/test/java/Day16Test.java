import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day16Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "7036"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/16/input", expected = "98484")
    })
    void part1(Stream<String> input, String expected) {
        var lines = input.toList();

        var map = new ReindeerMaze(lines);
        var runnersToEnd = map.solve();

        var min = runnersToEnd.stream().mapToInt(MazeRunner::calculateCost).min().getAsInt();

        assertEquals(Integer.parseInt(expected), min);
    }


	@Disabled("Couldn't solve tracking all paths; current solution takes hours to finish")
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "45"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/16/input", expected = "-1")
    })
    void part2(Stream<String> input, String expected) {
        var lines = input.toList();

        var map = new ReindeerMaze(lines);
        var runnersToEnd = map.solve(false);


        assertEquals(Integer.parseInt(expected), map.countFromEnd());
    }

    class ReindeerMaze {
        private final List<WObject> objects;
        private final Start start;
        private final End end;
        private final List<Wall> walls;
        private final Map<Point, Integer> minCost = new HashMap<>();
        private final Map<Point, List<MazeRunner>> minCostRunners = new HashMap<>();

        ReindeerMaze(List<String> input) {
            objects = IntStream.range(0, input.size())
                    .mapToObj(y -> IntStream.range(0, input.get(y).length())
                            .mapToObj(x -> getWObject(x, y, input.get(y).charAt(x))))
                    .flatMap(s -> s).filter(Objects::nonNull).toList();

            walls = objects.stream().map(w -> {
                if (w instanceof Wall s) {
                    return s;
                }
                return null;
            }).filter(Objects::nonNull).toList();

            start = (Start) objects.stream().filter(s -> s instanceof Start).findFirst().get();
            end = (End) objects.stream().filter(s -> s instanceof End).findFirst().get();
        }

        private WObject getWObject(int x, int y, char c) {
            return switch (c) {
                case '#' -> new Wall(x, y);
                case 'S' -> new Start(x, y);
                case 'E' -> new End(x, y);
                default -> null;
            };
        }

        int countFromEnd() {
            var runners = minCostRunners.get(end.getLocation());

            var points = new HashSet<>(runners.stream().flatMap(p -> p.visited.stream()).collect(Collectors.toSet()));

            while (!points.contains(start.getLocation())) {
                points.addAll(points.stream().flatMap(p -> minCostRunners.get(p).stream().flatMap(p2 -> p2.visited.stream())).collect(Collectors.toSet()));
            }

            printMazeWithPoints(points);

            // + END
            return points.size() + 1;
        }

        private void printMazeWithPoints(HashSet<Point> points) {
            var maxX = objects.stream().mapToInt(m -> m.location.x).max().getAsInt() + 1;
            var maxY = objects.stream().mapToInt(m -> m.location.y).max().getAsInt() + 1;

            IntStream.range(0, maxY).forEach(y -> {
                IntStream.range(0, maxX).forEach(x -> {
                    var o = objects.stream().filter(w -> w.collison(new Point(x, y))).findFirst();
                    if (o.isPresent()) {
                        var oo = o.get();
                        if (oo instanceof Wall) {
                            System.out.print("#");
                            return;
                        }
                        if (oo instanceof Start) {
                            System.out.print("S");
                            return;
                        }
                        if (oo instanceof End) {
                            System.out.print("E");
                            return;
                        }
                    } else {
                        if (points.contains(new Point(x, y))) {
                            System.out.print("O");
                        } else {
                            System.out.print(".");
                        }
                    }
                });
                System.out.println();
            });

        }

		List<MazeRunner> solve() {
			return solve(true);
		}
        List<MazeRunner> solve(boolean optimized) {
            var mazeRuns = new ArrayList<MazeRunner>();
            mazeRuns.add(new MazeRunner(start.getLocation()));

            int i = 0;

            while (mazeRuns.stream().anyMatch(m -> m.canMove(this))) {
                new ArrayList<>(mazeRuns).forEach(m -> {
                    mazeRuns.addAll(m.move(this));
                    // check solved
                    m.isSolved(end);
                });

                // stop bad runners
                for (var m : new ArrayList<>(mazeRuns)) {
                    var oldValue = minCost.get(m.getLocation());
                    var newCost = m.calculateCost();
                    if (oldValue == null) {
                        minCost.put(m.getLocation(), newCost);
                        minCostRunners.put(m.getLocation(), List.of(m));
                    } else {
                        if (oldValue < newCost && optimized) {
                            mazeRuns.remove(m);
                        } else if (oldValue > newCost) {
                            minCost.put(m.getLocation(), newCost);
                            minCostRunners.put(m.getLocation(), List.of(m));
                        } else if(oldValue == newCost) {
                            List<MazeRunner> oldList = new ArrayList<>(minCostRunners.get(m.getLocation()));
                            oldList.add(m);
                            minCostRunners.put(m.getLocation(), oldList);
                        }
                    }
                }
                mazeRuns.removeIf(m -> !m.canMove && !m.isSolved(end));

                System.out.println(i + " -> " + mazeRuns.stream().filter(m -> m.canMove).count());
                if (i++ % 100 == 0) {
                    printMaze(mazeRuns);
                }
            }
            printMaze(mazeRuns);

            return mazeRuns.stream().filter(m -> m.isSolved(end)).toList();
        }

        private void printMaze(List<MazeRunner> runners) {
            var maxX = objects.stream().mapToInt(m -> m.location.x).max().getAsInt() + 1;
            var maxY = objects.stream().mapToInt(m -> m.location.y).max().getAsInt() + 1;

            IntStream.range(0, maxY).forEach(y -> {
                IntStream.range(0, maxX).forEach(x -> {
                    var o = objects.stream().filter(w -> w.collison(new Point(x, y))).findFirst();
                    if (o.isPresent()) {
                        var oo = o.get();
                        if (oo instanceof Wall) {
                            System.out.print("#");
                            return;
                        }
                        if (oo instanceof Start) {
                            System.out.print("S");
                            return;
                        }
                        if (oo instanceof End) {
                            System.out.print("E");
                            return;
                        }
                    }

                    var r = runners.stream().filter(rrr -> rrr.canMove && rrr.location.equals(new Point(x, y))).toList();
                    if (!r.isEmpty()) {
                        if (r.size() < 10) {
                            System.out.print(r.size());
                        } else {
                            System.out.print("∞");
                        }
                    } else {
                        System.out.print(".");
                    }

                });
                System.out.println();
            });

        }
    }

    class MazeRunner {
        private Point location;
        private Dir dir;
        private boolean canMove = true;
        private final Set<Point> visited;

        private List<Movement> movements = new ArrayList<>();

        MazeRunner(Point location) {
            this.dir = Dir.EAST;
            this.location = location;
            this.visited = new HashSet<>();
            this.visited.add(location);
        }

        MazeRunner(Point location, Dir dir, Set<Point> visited, List<Movement> movements) {
            this.location = location;
            this.dir = dir;
            this.visited = new HashSet<>(visited);
            this.movements = new ArrayList<>(movements);
            this.movements.add(Movement.TURN);
            this.movements.add(Movement.MOVE);
        }

        public Point getLocation() {
            return location;
        }

        boolean isSolved(End end) {
            if (location.equals(end.getLocation())) {
                canMove = false;
                return true;
            }
            return false;
        }

        boolean canMove(ReindeerMaze maze) {
            if (canMove) {
                canMove = Arrays.stream(Dir.values()).map(d -> location.move(d)).anyMatch(n -> !visited.contains(n) && maze.walls.stream().noneMatch(w -> w.collison(n)));
                return canMove;
            }
            return false;
        }

        List<MazeRunner> move(ReindeerMaze maze) {
            if (canMove) {
                var nextLocations = Arrays.stream(Dir.values()).map(d -> location.move(d)).filter(n -> !visited.contains(n) && maze.walls.stream().noneMatch(w -> w.collison(n))).toList();

                if (nextLocations.isEmpty()) {
                    canMove = false;
                    isSolved(maze.end);
                    return List.of();
                }
                var oldLocation = location;
                var oldMovements = new ArrayList<>(movements);
                this.visited.add(location);
                var spawnNew = nextLocations.stream().filter(n -> !n.equals(location.move(dir))).toList();
                if (nextLocations.stream().anyMatch(n -> n.equals(location.move(dir)))) {
                    this.movements.add(Movement.MOVE);
                    this.location = location.move(dir);
                } else {
                    canMove = false;
                }
                return spawnNew.stream().map(l -> new MazeRunner(l, findDir(oldLocation, l), visited, oldMovements)).toList();
            }
            return List.of();
        }

        private Dir findDir(Point old, Point m) {
            if (old.move(Dir.NORTH).equals(m)) {
                return Dir.NORTH;
            }
            if (old.move(Dir.SOUTH).equals(m)) {
                return Dir.SOUTH;
            }
            if (old.move(Dir.EAST).equals(m)) {
                return Dir.EAST;
            }
            if (old.move(Dir.WEST).equals(m)) {
                return Dir.WEST;
            }
            throw new IllegalArgumentException();
        }

        public int calculateCost() {
            return movements.stream().mapToInt(m -> m == Movement.MOVE ? 1 : 1000).sum();
        }
    }


    enum Movement {
        MOVE,
        TURN
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

    record Point(int x, int y) {
        Point move(Dir d) {
            return new Point(x + d.x, y + d.y);
        }
    }

    abstract class WObject {
        private Point location;

        WObject(int x, int y) {
            this(new Point(x, y));
        }

        WObject(Point location) {
            this.location = location;
        }

        Point getLocation() {
            return location;
        }

        boolean collison(Point other) {
            return location.equals(other);
        }
    }

    class Wall extends WObject {
        Wall(int x, int y) {
            super(x, y);
        }
    }

    class Start extends WObject {
        Start(int x, int y) {
            super(x, y);
        }
    }

    class End extends WObject {
        End(int x, int y) {
            super(x, y);
        }
    }

}
