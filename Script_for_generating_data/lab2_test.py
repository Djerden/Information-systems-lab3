import httpx
import os
import asyncio
from pathlib import Path

# Константы
AUTH_URL = "http://localhost:8080/auth/sign-up"
IMPORT_URL = "http://localhost:8080/import/yaml"
TOKENS_FILE = "tokens.txt"
DATA_DIR = "data"

# Количество токенов и файлов для обработки
N = 20  # Укажите количество токенов и файлов для обработки

# Максимальное количество попыток при ошибке 429
MAX_RETRIES = 20

# Время ожидания (в секундах) между попытками при ошибке 429
RETRY_DELAY = 20  # Измени это значение на нужное тебе

# Функция для получения токенов
async def get_jwt_token(username, password):
    async with httpx.AsyncClient() as client:
        payload = {"username": username, "password": password}
        headers = {'Content-Type': 'application/json'}
        response = await client.post(AUTH_URL, json=payload, headers=headers)
        if response.status_code == 200:
            return response.json().get("token")
        else:
            print(f"Ошибка при получении токена для {username}: {response.status_code}")
            return None

# Функция для отправки YAML файла с использованием токена
async def send_yaml_file(thread_id, token, file_path):
    headers = {"Authorization": f"Bearer {token}"}
    async with httpx.AsyncClient(timeout=None) as client:
        with open(file_path, "rb") as file:
            files = {"file": file}
            attempt = 0
            while attempt < MAX_RETRIES:
                response = await client.post(IMPORT_URL, headers=headers, files=files)
                if response.status_code == 200:
                    print(f"Поток {thread_id}: Файл {file_path} успешно отправлен.")
                    break
                elif response.status_code == 429:
                    attempt += 1
                    print(f"Поток {thread_id}: Превышен лимит запросов. Попытка {attempt} из {MAX_RETRIES}.")
                    print(f"Ожидаем {RETRY_DELAY} секунд перед повтором...")
                    await asyncio.sleep(RETRY_DELAY)  # Пользовательская задержка перед повторной отправкой
                else:
                    print(f"Поток {thread_id}: Ошибка при отправке файла {file_path}: {response.status_code}")
                    break

# Основная логика скрипта
async def main():
    print('start')
    
    # Проверяем, существует ли файл с токенами
    if Path(TOKENS_FILE).exists():
        print("Найден файл с токенами. Загружаем токены...")
        with open(TOKENS_FILE, "r") as file:
            tokens = file.read().splitlines()
    else:
        print("Файл с токенами не найден. Создаем токены...")
        tokens = []
        async with httpx.AsyncClient() as client:
            for i in range(1, N + 1):
                username = f"test_user_{i}"
                password = "password"  # Предполагаем, что пароль одинаков для всех пользователей
                token = await get_jwt_token(username, password)
                if token:
                    tokens.append(token)

        # Сохраняем токены в файл
        with open(TOKENS_FILE, "w") as file:
            for token in tokens:
                file.write(token + "\n")

    # Проверяем, что токенов хватает
    if len(tokens) < N:
        print(f"Ошибка: получено {len(tokens)} токенов вместо {N}.")
        return

    # Получаем список YAML файлов
    yaml_files = [os.path.join(DATA_DIR, f) for f in os.listdir(DATA_DIR) if f.endswith('.yaml')]
    
    # Проверяем, что файлов хватает
    if len(yaml_files) < N:
        print(f"Ошибка: найдено {len(yaml_files)} YAML файлов вместо {N}.")
        return

    # Отбираем только первые N токенов и файлы
    tokens = tokens[:N]
    yaml_files = yaml_files[:N]
    
    print(f"Обрабатываем {N} токенов и {N} файлов")

    # Отправляем YAML файлы в многопотоке
    tasks = []
    for i, (token, file_path) in enumerate(zip(tokens, yaml_files), start=1):
        task = send_yaml_file(i, token, file_path)
        tasks.append(task)

    await asyncio.gather(*tasks)

# Запуск скрипта
if __name__ == "__main__":
    asyncio.run(main())
