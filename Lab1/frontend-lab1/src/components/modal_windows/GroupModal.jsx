import {useEffect, useState} from "react";
import Modal from "react-modal";
import CoordinatesModal from "./CoordinatesModal.jsx";
import PersonModal from "./PersonModal.jsx";

Modal.setAppElement("#root");

export default function GroupModal({
                                       isOpen,
                                       onRequestClose,
                                       group,
                                       editGroup
                                   }) {
    const [groupData, setGroupData] = useState({
        name: "",
        studentsCount: "",
        expelledStudents: "",
        transferredStudents: "",
        formOfEducation: "",
        shouldBeExpelled: "",
        semesterEnum: "",
        coordinatesId: "",
        groupAdminId: "",
    });

    // Подгружаем данные при изменении `group`
    useEffect(() => {
        if (group) {
            setGroupData({
                id: group.id,
                name: group.name || "",
                studentsCount: group.studentsCount || "",
                expelledStudents: group.expelledStudents || "",
                transferredStudents: group.transferredStudents || "",
                formOfEducation: group.formOfEducation || "",
                shouldBeExpelled: group.shouldBeExpelled || "",
                semesterEnum: group.semesterEnum || "",
                coordinatesId: group.coordinates?.id || "",
                groupAdminId: group.groupAdmin?.id || "",
            });
        } else {
            // Если редактируемой группы нет, сбрасываем данные
            setGroupData({
                name: "",
                studentsCount: "",
                expelledStudents: "",
                transferredStudents: "",
                formOfEducation: "",
                shouldBeExpelled: "",
                semesterEnum: "",
                coordinatesId: "",
                groupAdminId: "",
            });
        }
    }, [group]); // Выполняется при изменении `group`

    const [coordinates, setCoordinates] = useState([]);
    const [persons, setPersons] = useState([]);

    const [error, setError] = useState(""); // Поле для хранения ошибки

    const [isCoordinatesModalOpen, setIsCoordinatesModalOpen] = useState(false);
    const [isPersonModalOpen, setIsPersonModalOpen] = useState(false);

    const token = sessionStorage.getItem("jwt");

    // Функция для получения координат
    async function fetchCoordinates() {
        try {
            const response = await fetch("http://localhost:8080/coordinates", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!response.ok) throw new Error("Failed to fetch coordinates.");
            const data = await response.json();
            setCoordinates(data);
        } catch (error) {
            console.error(error);
        }
    }

    // Функция для получения персон
    async function fetchPersons() {
        try {
            const response = await fetch("http://localhost:8080/persons", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (!response.ok) throw new Error("Failed to fetch persons.");
            const data = await response.json();
            setPersons(data);
        } catch (error) {
            console.error(error);
        }
    }

    // Сброс данных формы
    function resetForm() {
        setGroupData({
            name: "",
            studentsCount: "",
            expelledStudents: "",
            transferredStudents: "",
            formOfEducation: "",
            shouldBeExpelled: "",
            semesterEnum: "",
            coordinatesId: "",
            groupAdminId: "",
        });
        setError("");
    }

    const handleSave = async () => {
        // Валидация обязательных полей
        if (!groupData.name.trim()) {
            setError("Group name is required and cannot be empty.");
            return;
        }
        if (!groupData.coordinatesId) {
            setError("Coordinates are required.");
            return;
        }
        if (!groupData.studentsCount || parseInt(groupData.studentsCount, 10) <= 0) {
            setError("Students count must be greater than 0.");
            return;
        }
        if (!groupData.expelledStudents || parseInt(groupData.expelledStudents, 10) <= 0) {
            setError("Expelled students must be greater than 0.");
            return;
        }
        if (
            groupData.transferredStudents &&
            parseInt(groupData.transferredStudents, 10) <= 0
        ) {
            setError("Transferred students must be greater than 0 (if specified).");
            return;
        }
        if (!groupData.shouldBeExpelled || parseInt(groupData.shouldBeExpelled, 10) <= 0) {
            setError("Should be expelled must be greater than 0.");
            return;
        }

        console.log(groupData)

        // Если валидация прошла успешно
        setError(""); // Сбрасываем ошибку

        const url = editGroup
            ? `http://localhost:8080/study-groups/${groupData.id}`
            : "http://localhost:8080/study-groups";

        const method = editGroup ? "PUT" : "POST";

        // console.log(groupData);
        // console.log(url)
        // console.log(method)
        // console.log(JSON.stringify(groupData))

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(groupData),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Error saving group.");
            }
            resetForm();
            onRequestClose();
        } catch (error) {
            setError(error.message); // Устанавливаем ошибку, если запрос не удался
        }
    };

    // Загрузка данных при открытии модального окна
    useEffect(() => {
        if (isOpen) {
            fetchCoordinates();
            fetchPersons();
        }
    }, [isOpen]);


    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onRequestClose}
            className="bg-white p-6 rounded shadow-md max-w-md mx-auto mt-20"
        >
            <h2 className="text-lg font-bold mb-4">
                {group ? "Edit Group" : "Create New Group"}
            </h2>

            {/* Сообщение об ошибке */}
            {error && <div className="mb-4 text-red-500 font-medium">{error}</div>}

            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Group Name (can't be empty)
                </label>
                <input
                    type="text"
                    value={groupData.name}
                    onChange={(e) => setGroupData({...groupData, name: e.target.value})}
                    className="w-full px-3 py-2 border rounded"
                />
            </div>

            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Coordinates
                </label>
                <div className="flex items-center space-x-2">
                    <select
                        value={groupData.coordinatesId}
                        onChange={(e) =>
                            setGroupData({...groupData, coordinatesId: e.target.value})
                        }
                        className="w-full px-3 py-2 border rounded"
                    >
                        <option value="">Select Coordinates</option>
                        {coordinates.map((coord) => (
                            <option key={coord.id} value={coord.id}>
                                X: {coord.x}, Y: {coord.y}
                            </option>
                        ))}
                    </select>
                    <button
                        onClick={() => setIsCoordinatesModalOpen(true)}
                        className="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600"
                    >
                        +
                    </button>
                </div>
            </div>

            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Admin (optional)
                </label>
                <div className="flex items-center space-x-2">
                    <select
                        value={groupData.groupAdminId}
                        onChange={(e) =>
                            setGroupData({...groupData, groupAdminId: e.target.value})
                        }
                        className="w-full px-3 py-2 border rounded"
                    >
                        <option value="">Select Admin</option>
                        {persons.map((person) => (
                            <option key={person.id} value={person.id}>
                                {person.name}
                            </option>
                        ))}
                    </select>
                    <button
                        onClick={() => setIsPersonModalOpen(true)}
                        className="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600"
                    >
                        +
                    </button>
                </div>
            </div>

            {/* Модальное окно для создания Coordinates */}
            <CoordinatesModal
                isOpen={isCoordinatesModalOpen}
                onRequestClose={() => setIsCoordinatesModalOpen(false)}
                onCoordinatesCreated={fetchCoordinates}
            />

            {/* Модальное окно для создания Person */}
            <PersonModal
                isOpen={isPersonModalOpen}
                onRequestClose={() => setIsPersonModalOpen(false)}
                onPersonCreated={fetchPersons}
            />


            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Students Count (must be greater than 0)
                </label>
                <input
                    type="number"
                    value={groupData.studentsCount}
                    onChange={(e) =>
                        setGroupData({...groupData, studentsCount: e.target.value})
                    }
                    className="w-full px-3 py-2 border rounded"
                />
            </div>
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Expelled Students (must be greater than 0)
                </label>
                <input
                    type="number"
                    value={groupData.expelledStudents}
                    onChange={(e) =>
                        setGroupData({...groupData, expelledStudents: e.target.value})
                    }
                    className="w-full px-3 py-2 border rounded"
                />
            </div>
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Transferred Students (must be greater than 0, optional)
                </label>
                <input
                    type="number"
                    value={groupData.transferredStudents}
                    onChange={(e) =>
                        setGroupData({...groupData, transferredStudents: e.target.value})
                    }
                    className="w-full px-3 py-2 border rounded"
                />
            </div>
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Form of Education (optional)
                </label>
                <select
                    value={groupData.formOfEducation || ""}
                    onChange={(e) =>
                        setGroupData({...groupData, formOfEducation: e.target.value || null})
                    }
                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                    <option value="">Not Specified</option>
                    <option value="DISTANCE_EDUCATION">Distance Education</option>
                    <option value="FULL_TIME_EDUCATION">Full Time Education</option>
                    <option value="EVENING_CLASSES">Evening Classes</option>
                </select>
            </div>
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Should Be Expelled (must be greater than 0)
                </label>
                <input
                    type="number"
                    value={groupData.shouldBeExpelled}
                    onChange={(e) =>
                        setGroupData({...groupData, shouldBeExpelled: e.target.value})
                    }
                    className="w-full px-3 py-2 border rounded"
                />
            </div>
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Semester (optional)
                </label>
                <select
                    value={groupData.semesterEnum || ""}
                    onChange={(e) =>
                        setGroupData({...groupData, semesterEnum: e.target.value || null})
                    }
                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                    <option value="">Not Specified</option>
                    <option value="FIRST">First</option>
                    <option value="THIRD">Third</option>
                    <option value="FIFTH">Fifth</option>
                    <option value="SIXTH">Sixth</option>
                    <option value="SEVENTH">Seventh</option>
                </select>
            </div>

            <div className="flex justify-end">
                <button
                    onClick={onRequestClose}
                    className="px-4 py-2 bg-gray-300 text-gray-700 rounded mr-2"
                >
                    Cancel
                </button>
                <button
                    onClick={handleSave}
                    className="px-4 py-2 bg-indigo-500 text-white rounded"
                >
                    Save
                </button>
            </div>

            {/* Модальное окно для создания Coordinates */}
            <CoordinatesModal
                isOpen={isCoordinatesModalOpen}
                onRequestClose={() => setIsCoordinatesModalOpen(false)}
                onCoordinatesCreated={fetchCoordinates}
            />

            {/* Модальное окно для создания Person */}
            <PersonModal
                isOpen={isPersonModalOpen}
                onRequestClose={() => setIsPersonModalOpen(false)}
                onPersonCreated={fetchPersons}
            />
        </Modal>
    );
}
