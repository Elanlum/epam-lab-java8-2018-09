package streams.part2.exercise;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"ConstantConditions", "unused"})
class Exercise1 {

    @Test
    void calcTotalYearsSpentInEpam() {
        List<Employee> employees = getEmployees();

        // TODO реализация

      Long hours = employees.stream()
            .flatMap(employee -> employee.getJobHistory().stream()
                .filter(job -> "EPAM".equals(job.getEmployer())))
            .mapToLong(JobHistoryEntry::getDuration)
            .sum();

        assertThat(hours, is(19L));
    }

    @Test
    void findPersonsWithQaExperience() {
        List<Employee> employees = getEmployees();

        // TODO реализация

      Set<Person> workedAsQa = employees.stream()
          .filter(employee -> employee.getJobHistory().stream()
              .anyMatch(jobHistoryEntry -> "QA".equalsIgnoreCase(jobHistoryEntry.getPosition())))
          .map(Employee::getPerson)
          .collect(Collectors.toSet());

        assertThat(workedAsQa, containsInAnyOrder(
                employees.get(2).getPerson(),
                employees.get(4).getPerson(),
                employees.get(5).getPerson()
        ));
    }

    @Test
    void composeFullNamesOfEmployeesUsingLineSeparatorAsDelimiter() {
        List<Employee> employees = getEmployees();

        // TODO реализация

      String result = employees.stream()
            .map(employee -> employee.getPerson().getFullName())
            .collect(Collectors.joining("\n"));

        assertThat(result, is(
                "Иван Мельников\n"
                + "Александр Дементьев\n"
                + "Дмитрий Осинов\n"
                + "Анна Светличная\n"
                + "Игорь Толмачёв\n"
                + "Иван Александров"));
    }

    @Test
    @SuppressWarnings("Duplicates")
    void groupPersonsByFirstPositionUsingToMap() {
        List<Employee> employees = getEmployees();

        // TODO реализация
      Function<Employee, String> getFirstPosition = employee -> employee.getJobHistory().get(0).getPosition();
      Function<Employee, Set<Person>> getSetOfPerson = employee -> {
        Set<Person> set = new HashSet<>();
        set.add(employee.getPerson());
        return set;
      };
      Map<String, Set<Person>> result =  employees.stream()
            .collect(toMap(getFirstPosition, getSetOfPerson, (people, people2) -> {
              Set<Person> res = new HashSet<>();
              res.addAll(people);
              res.addAll(people2);
              return res;
            }));

        assertThat(result, hasEntry(is("dev"), contains(employees.get(0).getPerson())));
        assertThat(result, hasEntry(is("QA"), containsInAnyOrder(employees.get(2).getPerson(), employees.get(5).getPerson())));
        assertThat(result, hasEntry(is("tester"), containsInAnyOrder(employees.get(1).getPerson(), employees.get(3).getPerson(), employees.get(4).getPerson())));
    }

    @Test
    @SuppressWarnings("Duplicates")
    void groupPersonsByFirstPositionUsingGroupingByCollector() {
        List<Employee> employees = getEmployees();

        // TODO реализация
//        Map<String, Set<Person>> result = null;

      Map<String, Set<Person>> result = employees.stream()
            .collect(groupingBy(employee -> employee.getJobHistory()
                .stream()
                .findFirst()
                .orElseThrow(UnsupportedOperationException::new)
                .getPosition(), Collectors.toSet()))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()
                .stream()
                .map(Employee::getPerson)
                .collect(Collectors.toSet())));

        assertThat(result, hasEntry(is("dev"), contains(employees.get(0).getPerson())));
        assertThat(result, hasEntry(is("QA"), containsInAnyOrder(employees.get(2).getPerson(), employees.get(5).getPerson())));
        assertThat(result, hasEntry(is("tester"), containsInAnyOrder(employees.get(1).getPerson(), employees.get(3).getPerson(), employees.get(4).getPerson())));
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
