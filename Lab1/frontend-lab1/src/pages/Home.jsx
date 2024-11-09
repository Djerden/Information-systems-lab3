export default function Home() {
    return (
        <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">
            <h1 className="text-3xl font-bold mb-4 text-indigo-600">Лабораторная работа №1</h1>
            <p className="text-gray-700 mb-4"><strong>Вариант 444412</strong></p>
            <p className="text-gray-700 mb-4">
                <strong>По согласованию с практиком Java EE заменен на Spring</strong>
            </p>
            <p className="text-gray-700 mb-4">
                Реализовать информационную систему, которая позволяет взаимодействовать с объектами класса <code>StudyGroup</code>, описанными следующим образом:
            </p>
            <pre className="bg-gray-100 p-4 rounded-md overflow-x-auto mb-4">
                <code>
                    {`public class StudyGroup {
    private Long id; // Поле не может быть null, должно быть уникальным и генерироваться автоматически
    private String name; // Поле не может быть null, строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private LocalDate creationDate; // Поле не может быть null, генерируется автоматически
    private long studentsCount; // Значение должно быть больше 0
    private int expelledStudents; // Значение должно быть больше 0
    private Long transferredStudents; // Может быть null, значение должно быть больше 0
    private FormOfEducation formOfEducation; // Может быть null
    private long shouldBeExpelled; // Значение должно быть больше 0
    private Semester semesterEnum; // Может быть null
    private Person groupAdmin; // Может быть null
}

public class Coordinates { 
    private float x; 
    private double y; 
}

public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Color eyeColor; //Поле не может быть null
    private Color hairColor; //Поле не может быть null
    private Location location; //Поле может быть null
    private float weight; //Значение поля должно быть больше 0
    private Country nationality; //Поле не может быть null
}

public class Location {
    private Float x; //Поле не может быть null
    private Integer y; //Поле не может быть null
    private String name; //Поле может быть null
}

public enum FormOfEducation { 
    DISTANCE_EDUCATION, 
    FULL_TIME_EDUCATION, 
    EVENING_CLASSES; 
}
public enum Semester { 
    FIRST, 
    THIRD, 
    FIFTH, 
    SIXTH, 
    SEVENTH; 
}
public enum Color { 
    GREEN, 
    BLACK, 
    WHITE, 
    BROWN; 
}
public enum Country { 
    GERMANY, 
    FRANCE, 
    CHINA, 
    ITALY; 
}
`}
                </code>
            </pre>
            <h2 className="text-2xl font-semibold text-indigo-500 mb-2">Требования к системе:</h2>
            <ul className="list-disc pl-6 space-y-2 text-gray-700 mb-6">
                <li>Создание, получение, обновление и удаление объектов <code>StudyGroup</code>.</li>
                <li>Автоматическая синхронизация изменений на сервере и в базе данных.</li>
                <li>Пагинация и фильтрация списка объектов на главной странице.</li>
                <li>Поддержка ролей: незарегистрированные пользователи, обычные пользователи, администраторы.</li>
                <li>Поддержка авторизации и регистрации с ограничениями по правам.</li>
            </ul>
            <h2 className="text-2xl font-semibold text-indigo-500 mb-2">Операции с объектами:</h2>
            <ul className="list-disc pl-6 space-y-2 text-gray-700 mb-6">
                <li>Вернуть объект с минимальным значением <code>expelledStudents</code>.</li>
                <li>Подсчитать количество объектов с определенным значением <code>groupAdmin</code>.</li>
                <li>Найти объекты с подстрокой в поле <code>name</code>.</li>
                <li>Отчислить всех студентов из указанной группы.</li>
                <li>Добавить студента в указанную группу.</li>
            </ul>
            <h2 className="text-2xl font-semibold text-indigo-500 mb-2">Особенности базы данных:</h2>
            <ul className="list-disc pl-6 space-y-2 text-gray-700">
                <li>Использование PostgreSQL для хранения объектов.</li>
                <li>Генерация <code>id</code> с помощью базы данных.</li>
                <li>Хранение паролей с хэшированием MD5.</li>
                <li>Сохранение информации о пользователях, создавших объекты, и времени изменения данных.</li>
            </ul>
            <h2 className="text-2xl font-semibold text-indigo-500 mt-6 mb-2">Содержание отчета:</h2>
            <p className="text-gray-700">
                1. Текст задания.
                <br />
                2. UML-диаграммы классов и пакетов.
                <br />
                3. Исходный код системы.
                <br />
                4. Выводы по работе.
            </p>
        </div>
    );
}
