import { useState } from "react";
import Modal from "react-modal";

Modal.setAppElement("#root");

export default function CoordinatesModal({ isOpen, onRequestClose}) {
    const [coordinatesData, setCoordinatesData] = useState({
        x: "",
        y: "",
    });

    const [error, setError] = useState(""); // Хранение сообщения об ошибке

    const token = sessionStorage.getItem("jwt");

    function resetForm() {
        setCoordinatesData({
            x: "",
            y: "",
        });
        setError(""); // Сброс ошибок
    }

    async function handleSave() {
        try {
            // Проверяем обязательные поля
            if (!coordinatesData.x.trim() || !coordinatesData.y.trim()) {
                setError("Fields 'X' and 'Y' are required.");
                return;
            }

            // Проверяем, что X и Y — числа
            const parsedX = parseFloat(coordinatesData.x);
            const parsedY = parseFloat(coordinatesData.y);

            if (isNaN(parsedX) || isNaN(parsedY)) {
                setError("Fields 'X' and 'Y' must be valid numbers.");
                return;
            }

            // Формируем данные для отправки
            const dataToSend = {
                x: parsedX,
                y: parsedY,
            };

            const response = await fetch("http://localhost:8080/coordinates", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(dataToSend),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to save coordinates.");
            }

            resetForm(); // Сбрасываем форму при успешном сохранении
            onRequestClose(); // Закрываем модальное окно
        } catch (error) {
            setError(error.message); // Устанавливаем ошибку для отображения пользователю
        }
    }

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={() => {
                resetForm(); // Сбрасываем данные при закрытии окна
                onRequestClose();
            }}
            contentLabel="Create Coordinates"
            className="bg-white p-6 rounded shadow-md max-w-md mx-auto mt-20"
        >
            <h2 className="text-lg font-bold mb-4">Create New Coordinates</h2>

            {/* Сообщение об ошибке */}
            {error && <div className="mb-4 text-red-500 font-medium">{error}</div>}

            {/* Поле для X */}
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    X Coordinate
                </label>
                <input
                    type="text"
                    value={coordinatesData.x}
                    onChange={(e) =>
                        setCoordinatesData({ ...coordinatesData, x: e.target.value })
                    }
                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
            </div>

            {/* Поле для Y */}
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Y Coordinate
                </label>
                <input
                    type="text"
                    value={coordinatesData.y}
                    onChange={(e) =>
                        setCoordinatesData({ ...coordinatesData, y: e.target.value })
                    }
                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
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
    );
}
