package lambda.part3.exercise;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"unused", "ConstantConditions"})
class Exercise2 {

    private static class MapHelper<T> {

        private final List<T> source;

        private MapHelper(List<T> source) {
            this.source = source;
        }

        public static <T> MapHelper<T> from(List<T> source) {
            return new MapHelper<>(source);
        }

        public List<T> getMapped() {
            return new ArrayList<>(source);
        }

        /**
         * Создает объект для маппинга, передавая ему новый список, построенный на основе исходного.
         * Для добавления в новый список каждый элемент преобразовывается с использованием заданной функции.
         * ([T], (T -> R)) -> [R]
         * @param mapping Функция преобразования элементов.
         */
        public <R> MapHelper<R> map(Function<T, R> mapping) {
            return getMapHelper(mapping);
        }

        //применяет функцию для каждого элемента исходного списка и кладет его в новый список
        @NotNull
        private <R> MapHelper<R> getMapHelper(Function<T, R> mapping) {
            List<R> rList = new ArrayList<>();
            source.forEach(element -> rList.add(mapping.apply(element)));
            return from(rList);
        }

        /**
         * Создает объект для маппинга, передавая ему новый список, построенный на основе исходного.
         * Для добавления в новый список каждый элемент преобразовывается в список с использованием заданной функции.
         * ([T], (T -> [R])) -> [R]
         * @param flatMapping Функция преобразования элементов.
         */
        public <R> MapHelper<R> flatMap(Function<T, List<R>> flatMapping) {
            List<R> newList = new ArrayList<>();
            source.forEach(element -> {
                List<R> tempList = flatMapping.apply(element);//после применения функции получается список элементов
                tempList.forEach(e -> newList.add(e));//каждый из которых кладем в заготовленный список
            });
            source.forEach(element -> {
//                List<R> tempList = flatMapping.apply(element);
//                newList.addAll(tempList);
//                List<R> tempList = newList.addAll(flatMapping.apply(element)))
            });
            return from(newList);
        }
    }

    @Test
    void mapEmployeesToLengthOfTheirFullNamesUsingMapHelper() {
        List<Employee> employees = getEmployees();

        // TODO                 MapHelper.from(employees)
        // TODO                          .map(Employee -> Person)
        // TODO                          .map(Person -> String(full name))
        // TODO                          .map(String -> Integer(length of string))
        // TODO                          .getMapped();
        List<Integer> lengths = MapHelper.from(employees)
            .map(Employee::getPerson)
            .map(Person::getFullName)
            .map(String::length)
            .getMapped();
        assertThat(lengths, contains(14, 19, 14, 15, 14, 16));
    }

    @Test
    void mapEmployeesToCodesOfLetterTheirPositionsUsingMapHelper() {
        List<Employee> employees = getEmployees();

        // TODO               MapHelper.from(employees)
        // TODO                        .flatMap(Employee -> JobHistoryEntry)
        // TODO                        .map(JobHistoryEntry -> String(position))
        // TODO                        .flatMap(String -> Character(letter))
        // TODO                        .map(Character -> Integer(code letter)
        // TODO                        .getMapped();
        List<Integer> codes = MapHelper.from(employees)
            .flatMap(Employee::getJobHistory)
            .map(JobHistoryEntry::getPosition)
            .flatMap(s -> {
                List<Character> list = new ArrayList<>();
                for (char c: s.toCharArray()){
                    list.add(c);
                }
                System.out.println(list);
                return list;
            })
            .map(character -> (int)character)
            .getMapped();
        assertThat(codes, contains(calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev", "tester", "tester", "QA", "QA", "QA", "dev").toArray()));
    }

//    public static void main(String[] args) {
//        List<Integer> integers = calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev",
//            "tester", "tester",
//            "QA", "QA", "QA", "dev");
//        integers.forEach(element -> System.out.println(element));
//    }

    private static List<Integer> calcCodes(String...strings) {
        List<Integer> codes = new ArrayList<>();
        for (String string : strings) {
            for (char letter : string.toCharArray()) {
                codes.add((int) letter);
            }
        }
        return codes;
    }

    private static List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("Иван", "Мельников", 30),
                        Arrays.asList(
                                new JobHistoryEntry(2, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Александр", "Дементьев", 28),
                        Arrays.asList(
                                new JobHistoryEntry(1, "tester", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Дмитрий", "Осинов", 40),
                        Arrays.asList(
                                new JobHistoryEntry(3, "QA", "yandex"),
                                new JobHistoryEntry(1, "QA", "mail.ru"),
                                new JobHistoryEntry(1, "dev", "mail.ru")
                        )),
                new Employee(
                        new Person("Анна", "Светличная", 21),
                        Collections.singletonList(
                                new JobHistoryEntry(1, "tester", "T-Systems")
                        )),
                new Employee(
                        new Person("Игорь", "Толмачёв", 50),
                        Arrays.asList(
                                new JobHistoryEntry(5, "tester", "EPAM"),
                                new JobHistoryEntry(6, "QA", "EPAM")
                        )),
                new Employee(
                        new Person("Иван", "Александров", 33),
                        Arrays.asList(
                                new JobHistoryEntry(2, "QA", "T-Systems"),
                                new JobHistoryEntry(3, "QA", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM")
                        ))
        );
    }
}
