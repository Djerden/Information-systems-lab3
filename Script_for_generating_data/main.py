import random
import os
from faker import Faker
import yaml
from datetime import datetime

# Инициализируем Faker
fake = Faker()

# Список возможных значений для Enum
form_of_education = ['DISTANCE_EDUCATION', 'FULL_TIME_EDUCATION', 'EVENING_CLASSES']
semester_enum = ['FIRST', 'THIRD', 'FIFTH', 'SIXTH', 'SEVENTH']
colors = ['GREEN', 'BLACK', 'WHITE', 'BROWN']
countries = ['GERMANY', 'FRANCE', 'CHINA', 'ITALY']

# Генерация случайного объекта Coordinates
def generate_coordinates():
    return {
        'x': random.uniform(-180, 180),
        'y': random.uniform(-90, 90),
    }

# Генерация случайного объекта Location
def generate_location():
    return {
        'x': random.uniform(-180, 180),
        'y': random.randint(-90, 90),
        'name': fake.city(),
    }

# Генерация случайного объекта Person
def generate_person():
    return {
        'name': fake.name(),
        'eye_color': random.choice(colors),
        'hair_color': random.choice(colors),
        'location': generate_location(),
        'weight': random.uniform(40, 100),
        'nationality': random.choice(countries),
    }

# Генерация случайного объекта StudyGroup
def generate_study_group():
    return {
        'name': fake.word() + ' Group',
        'coordinates': generate_coordinates(),
        'students_count': random.randint(20, 100),
        'expelled_students': random.randint(1, 50),
        'transferred_students': random.randint(1, 50),
        'form_of_education': random.choice(form_of_education),
        'should_be_expelled': random.randint(1, 50),
        'semester_enum': random.choice(semester_enum),
        'group_admin': generate_person(),
    }

# Функция для генерации данных с заданным размером
def generate_data_file(file_size_mb, num_files):
    # Примерная оценка размера одного объекта
    avg_object_size = 480  # в байтах (примерное значение)
    
    # Рассчитываем необходимое количество объектов для одного файла
    objects_per_file = (file_size_mb * 1024 * 1024) // avg_object_size

    # Создаем папку 'data', если она не существует
    os.makedirs('data', exist_ok=True)

    for i in range(num_files):
        data = [generate_study_group() for _ in range(objects_per_file)]
        
        # Создаем имя файла
        file_name = os.path.join('data', f"study_groups_{i+1}.yaml")
        
        # Запись данных в YAML файл
        with open(file_name, 'w') as file:
            yaml.dump(data, file, default_flow_style=False, allow_unicode=True)
        
        # Проверяем размер файла
        file_size = os.path.getsize(file_name) / (1024 * 1024)  # в МБ
        print(f"Файл {file_name} был создан. Количество объектов: {len(data)}. Размер: {file_size:.2f} МБ")

# Получаем ввод от пользователя
file_size_mb = int(input("Введите желаемый размер файла (в мегабайтах): "))
num_files = int(input("Введите количество файлов, которые нужно создать: "))

# Генерируем данные и создаем файлы
generate_data_file(file_size_mb, num_files)
