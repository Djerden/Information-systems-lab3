import { useState, useEffect } from "react";
import Modal from "react-modal";
import LocationModal from "./LocationModal";

Modal.setAppElement("#root");

export default function PersonModal({ isOpen, onRequestClose, onPersonCreated}) {
    const [personData, setPersonData] = useState({
        name: "",
        eyeColor: "",
        hairColor: "",
        locationId: "",
        weight: "",
        nationality: "",
    });

    const [locations, setLocations] = useState([]); // Список доступных локаций
    const [isLocationModalOpen, setIsLocationModalOpen] = useState(false);
    const [error, setError] = useState(""); // Для вывода ошибок

    const token = sessionStorage.getItem("jwt");

    // Получение списка локаций
    async function fetchLocations() {
        try {
            const response = await fetch("http://localhost:8080/locations", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!response.ok) {
                throw new Error("Failed to fetch locations.");
            }
            const data = await response.json();
            setLocations(data);
        } catch (error) {
            console.error("Ошибка получения локаций:", error);
        }
    }

    // Сброс данных формы
    function resetForm() {
        setPersonData({
            name: "",
            eyeColor: "",
            hairColor: "",
            locationId: "",
            weight: "",
            nationality: "",
        });
        setError(""); // Сброс ошибок
    }

    // Сохранение Person
    async function handleSave() {
        try {
            // Проверяем обязательные поля с подробными подсказками
            if (!personData.name.trim()) {
                setError("Name is required and cannot be empty.");
                return;
            }

            if (!personData.eyeColor) {
                setError("Eye Color is required.");
                return;
            }

            if (!personData.hairColor) {
                setError("Hair Color is required.");
                return;
            }

            if (!personData.weight || parseFloat(personData.weight) <= 0) {
                setError("Weight must be greater than 0.");
                return;
            }

            if (!personData.nationality) {
                setError("Nationality is required.");
                return;
            }

            // Подготовка данных для отправки
            const dataToSend = {
                ...personData,
                weight: parseFloat(personData.weight), // Преобразуем строку в число
            };

            const response = await fetch("http://localhost:8080/persons", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(dataToSend),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to save person.");
            }

            // Вызываем колбэк после успешного сохранения
            if (onPersonCreated) {
                onPersonCreated();
            }

            resetForm(); // Сбрасываем форму после успешного сохранения
            onRequestClose(); // Закрываем модальное окно
        } catch (error) {
            setError(error.message); // Устанавливаем сообщение об ошибке
        }
    }

    // Загрузка локаций при открытии модального окна
    useEffect(() => {
        if (isOpen) {
            fetchLocations();
        }
    }, [isOpen]);

    return (
        <>
            <Modal
                isOpen={isOpen}
                onRequestClose={() => {
                    resetForm(); // Сброс данных формы
                    onRequestClose();
                }}
                contentLabel="Create Person"
                className="bg-white p-6 rounded shadow-md max-w-md mx-auto mt-20"
            >
                <h2 className="text-lg font-bold mb-4">Create New Person</h2>

                {/* Сообщение об ошибке */}
                {error && <div className="mb-4 text-red-500 font-medium">{error}</div>}

                {/* Поле для Name */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Name (must be unique)
                    </label>
                    <input
                        type="text"
                        value={personData.name}
                        onChange={(e) =>
                            setPersonData({ ...personData, name: e.target.value })
                        }
                        className="w-full px-3 py-2 border rounded"
                    />
                </div>

                {/* Поле для Eye Color */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Eye Color
                    </label>
                    <select
                        value={personData.eyeColor}
                        onChange={(e) =>
                            setPersonData({ ...personData, eyeColor: e.target.value })
                        }
                        className="w-full px-3 py-2 border rounded"
                    >
                        <option value="">Select Eye Color</option>
                        <option value="GREEN">Green</option>
                        <option value="BLACK">Black</option>
                        <option value="WHITE">White</option>
                        <option value="BROWN">Brown</option>
                    </select>
                </div>

                {/* Поле для Hair Color */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Hair Color
                    </label>
                    <select
                        value={personData.hairColor}
                        onChange={(e) =>
                            setPersonData({ ...personData, hairColor: e.target.value })
                        }
                        className="w-full px-3 py-2 border rounded"
                    >
                        <option value="">Select Hair Color</option>
                        <option value="GREEN">Green</option>
                        <option value="BLACK">Black</option>
                        <option value="WHITE">White</option>
                        <option value="BROWN">Brown</option>
                    </select>
                </div>

                {/* Поле для Location */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Location
                    </label>
                    <div className="flex space-x-2">
                        <select
                            value={personData.locationId}
                            onChange={(e) =>
                                setPersonData({ ...personData, locationId: e.target.value })
                            }
                            className="w-full px-3 py-2 border rounded"
                        >
                            <option value="">Select Location</option>
                            {locations.map((location) => (
                                <option key={location.id} value={location.id}>
                                    X: {location.x}, Y: {location.y}{" "}
                                    {location.name ? `(${location.name})` : ""}
                                </option>
                            ))}
                        </select>
                        <button
                            onClick={() => setIsLocationModalOpen(true)}
                            className="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600"
                        >
                            +
                        </button>
                    </div>
                </div>

                {/* Поле для Weight */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Weight (must be greater than 0)
                    </label>
                    <input
                        type="number"
                        value={personData.weight}
                        onChange={(e) =>
                            setPersonData({ ...personData, weight: e.target.value })
                        }
                        className="w-full px-3 py-2 border rounded"
                    />
                </div>

                {/* Поле для Nationality */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Nationality
                    </label>
                    <select
                        value={personData.nationality}
                        onChange={(e) =>
                            setPersonData({ ...personData, nationality: e.target.value })
                        }
                        className="w-full px-3 py-2 border rounded"
                    >
                        <option value="">Select Nationality</option>
                        <option value="GERMANY">Germany</option>
                        <option value="FRANCE">France</option>
                        <option value="CHINA">China</option>
                        <option value="ITALY">Italy</option>
                    </select>
                </div>

                <div className="flex justify-end">
                    <button
                        onClick={() => {
                            resetForm(); // Сброс данных при отмене
                            onRequestClose();
                        }}
                        className="px-4 py-2 bg-gray-300 text-gray-700 rounded mr-2"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSave}
                        className="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600"
                    >
                        Save
                    </button>
                </div>
            </Modal>

            {/* Модальное окно для создания Location */}
            <LocationModal
                isOpen={isLocationModalOpen}
                onRequestClose={() => setIsLocationModalOpen(false)}
                onLocationCreated={fetchLocations}
            />
        </>
    );
}
