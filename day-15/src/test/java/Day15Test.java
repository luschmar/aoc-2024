import org.junit.jupiter.params.ParameterizedTest;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day15Test {
    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "10092"),
            @AocInputMapping(input = "test2.txt", expected = "2028"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/15/input", expected = "1463715")
    })
    void part1(Stream<String> input, String expected) {
        var data = input.toList();

        var warehouse = new Warehouse(data);

        var instructions = data.stream().filter(s -> !s.startsWith("#") && !s.isBlank()).flatMap(l -> l.codePoints().mapToObj(this::readDir)).toList();

        System.out.println("Initial");
        warehouse.printMap();

        for (var i : instructions) {
            warehouse.moveRobot(i);
        }


        assertEquals(Long.parseLong(expected), warehouse.gps());
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "test.txt", expected = "9021"),
            @AocInputMapping(input = "testWide.txt", expected = "618"),
            @AocInputMapping(input = "https://adventofcode.com/2024/day/15/input", expected = "1481392")
            //@AocInputMapping(input = "debug.txt", expected = "618")
    })
    void part2(Stream<String> input, String expected) throws IOException {
        var data = input.toList();

        var warehouse = new Warehouse(data, true);

        var instructions = data.stream().filter(s -> !s.startsWith("#") && !s.isBlank()).flatMap(l -> l.codePoints().mapToObj(this::readDir)).toList();

        System.out.println("Initial");
        warehouse.printMap();

        int p = 0;
        for (var i : instructions) {
            System.out.println("Move " + i + " " + ((100 * p++ / instructions.size())) + " " + p);
            warehouse.moveRobot(i);

            //warehouse.printMap();
            //System.out.println();

        }

        warehouse.printMap();

        assertEquals(Long.parseLong(expected), warehouse.gps());
    }

    @ParameterizedTest
    @AocFileSource(inputs = {
            @AocInputMapping(input = "debug.txt", expected = "12385")
    })
    void debug(Stream<String> input, String expected) throws IOException {
        var data = input.toList();
        var warehouse = new Warehouse(data);
        var instructions = data.stream().filter(s -> !s.startsWith("#") && !s.isBlank()).flatMap(l -> l.codePoints().mapToObj(this::readDir)).toList();
        System.out.println("Initial");
        warehouse.printMap();
        int p = 0;
        for (var i : instructions) {
            System.out.println("Move " + i + " " + ((100 * p++ / instructions.size())) + " " + p);
            warehouse.moveRobot(i);

            warehouse.printMap();
            System.out.println();

        }
        warehouse.printMap();
        assertEquals(Long.parseLong(expected), warehouse.gps());
    }


    private Dir readDir(int codePoint) {
        var c = Character.toChars(codePoint)[0];
        if (c == '<') {
            return Dir.WEST;
        } else if (c == '^') {
            return Dir.NORTH;
        } else if (c == 'v') {
            return Dir.SOUTH;
        } else if (c == '>') {
            return Dir.EAST;
        }
        return null;
    }

    class Warehouse {

        private final List<WObject> objects;
        private final Robot robot;

        Warehouse(List<String> input) {
            this(input, false);
        }

        Warehouse(List<String> input, boolean wide) {
            List<String> inputMod = input.stream().map(s -> wide ? inputMod(s) : s).toList();

            objects = IntStream.range(0, inputMod.size())
                    .mapToObj(y -> IntStream.range(0, inputMod.get(y).length())
                            .mapToObj(x -> getWObject(x, y, inputMod.get(y).charAt(x))))
                    .flatMap(s -> s).filter(Objects::nonNull).toList();

            robot = (Robot) objects.stream().filter(s -> s instanceof Robot).findFirst().get();
        }

        private String inputMod(String s) {
            //System.out.println(s);
            var mod = s.replaceAll("\\.", "..").replaceAll("#", "##").replaceAll("O", "[]").replaceAll("@", "@.");
            System.out.println(mod);
            return mod;
        }

        long gps() {
            return objects.stream().filter(k -> (k instanceof Box) || (k instanceof BoxWide)).mapToLong(b -> b.location.y() * 100L + b.location.x()).sum();
        }

        void printMap() {
            var maxX = objects.stream().mapToInt(o -> o.location.x()).max().getAsInt() + 1;
            var maxY = objects.stream().mapToInt(o -> o.location.y()).max().getAsInt() + 1;

            IntStream.range(0, maxY).forEach(y -> {
                IntStream.range(0, maxX).forEach(x -> {
                    var obj = objects.stream().filter(o -> o.collison(new Point(x, y))).findFirst();
                    if (obj.isPresent()) {
                        var wobj = obj.get();
                        switch (wobj) {
                            case Wall wall -> System.out.print("\u001B[32m#\u001B[0m");
                            case Box box -> System.out.print("\u001B[34mO\u001B[0m");
                            case Robot robot1 -> System.out.print("\u001B[31m@\u001B[0m");
                            case BoxWide bw -> {
                                if (bw.isLeft(new Point(x, y))) {
                                    System.out.print("\u001B[34m[");
                                } else {
                                    System.out.print("]\u001B[0m");
                                }
                            }
                            default -> {
                            }
                        }
                    } else {
                        System.out.print(".");
                    }
                });
                System.out.println();
                System.out.print("\033[H\033[2J");
                System.out.flush();
            });
        }

        void moveRobot(Dir d) {
            var ob = getMovableObjects(robot, d);
            for (var o : ob) {
                o.move(d);
            }
        }

        private Set<WObject> getMovableObjects(WObject o, Dir d) {
            var neighbors = o.neighbors(d);
            var next = objects.stream().filter(obj -> neighbors.stream().anyMatch(obj::collison)).toList();
            // Good, Object can move there...
            if (next.isEmpty()) {
                return Set.of(o);
            }

            // Oh no we hit a wall
            if (next.stream().anyMatch(w -> w instanceof Wall)) {
                return Set.of();
            }

            List<Set<WObject>> moveable = new ArrayList<>();
            for (var a : next) {
                var aa = getMovableObjects(a, d);
                moveable.add(aa);
            }

            // oh no, one object cannot be moved!!!
            if (moveable.stream().anyMatch(Set::isEmpty)) {
                return Set.of();
            }

            return Stream.concat(moveable.stream().flatMap(Collection::stream), Stream.of(o)).collect(Collectors.toSet());
        }

        WObject getWObject(int x, int y, char object) {
            if (object == '#') {
                return new Wall(x, y);
            } else if (object == '@') {
                return new Robot(x, y);
            } else if (object == 'O') {
                return new Box(x, y);
            } else if (object == '[') {
                return new BoxWide(x, y);
            }
            return null;
        }

        public boolean checkViolation() {
            return objects.stream().anyMatch(l -> objects.stream().filter(r -> l != r).anyMatch(r -> l.collison(r.location)));
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

        public List<Point> neighbors(Dir d) {
            return List.of(location.move(d));
        }

        Point getLocation() {
            return location;
        }

        void move(Dir d) {
            location = location.move(d);
        }

        boolean collison(Point other) {
            return location.equals(other);
        }
    }

    class Robot extends WObject {
        Robot(int x, int y) {
            super(x, y);
        }
    }

    class BoxWide extends WObject {

        BoxWide(int x, int y) {
            super(x, y);
        }

        @Override
        public boolean collison(Point p) {
            return super.collison(p) || super.collison(p.move(Dir.WEST));
        }

        @Override
        public List<Point> neighbors(Dir d) {
            if (d == Dir.WEST) {
                return super.neighbors(d);
            }
            if (d == Dir.EAST) {
                return List.of(getLocation().move(Dir.EAST).move(Dir.EAST));
            }
            if (d == Dir.NORTH) {
                return List.of(getLocation().move(Dir.NORTH), getLocation().move(Dir.NORTH).move(Dir.EAST));
            }
            return List.of(getLocation().move(Dir.SOUTH), getLocation().move(Dir.SOUTH).move(Dir.EAST));
        }

        public boolean isLeft(Point p) {
            return super.collison(p);
        }
    }

    class Box extends WObject {
        Box(int x, int y) {
            super(x, y);
        }
    }

    class Wall extends WObject {
        Wall(int x, int y) {
            super(x, y);
        }

        @Override
        public void move(Dir d) {
            throw new IllegalArgumentException();
        }
    }

    public static void ClearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
