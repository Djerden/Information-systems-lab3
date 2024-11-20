import {useState, useEffect} from "react";
import GroupModal from "../../components/modal_windows/GroupModal.jsx";
import PersonModal from "../../components/modal_windows/PersonModal.jsx";
import CoordinatesModal from "../../components/modal_windows/CoordinatesModal.jsx";
import LocationModal from "../../components/modal_windows/LocationModal.jsx";


export default function Groups() {
    const [groups, setGroups] = useState([]);
    const [coordinates, setCoordinates] = useState([]);
    const [persons, setPersons] = useState([]);
    const [locations, setLocations] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);

    const [editingGroup, setEditingGroup] = useState(null);
    const token = sessionStorage.getItem("jwt");

    // Константы модальных окон
    const [isGroupModalOpen, setIsGroupModalOpen] = useState(false);
    const [isPersonModalOpen, setIsPersonModalOpen] = useState(false);
    const [isCoordinatesModalOpen, setIsCoordinatesModalOpen] = useState(false);
    const [isLocationModalOpen, setIsLocationModalOpen] = useState(false);



    // Загружаем группы с сервера
    const fetchGroups = async (page = 0) => {
        try {
            const response = await fetch(
                `http://localhost:8080/api/study-groups?page=${page}&size=10&sort=id,asc`,
                {headers: {Authorization: `Bearer ${token}`}}
            );
            if (!response.ok) throw new Error("Ошибка загрузки групп");
            const data = await response.json();
            setGroups(data.content);
            setTotalPages(data.totalPages);
            setCurrentPage(data.number);
        } catch (error) {
            console.error(error);
        }
    };

    // Загружаем вспомогательные объекты
    const fetchCoordinates = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/coordinates", {
                headers: {Authorization: `Bearer ${token}`},
            });
            if (!response.ok) throw new Error("Ошибка загрузки координат");
            const data = await response.json();
            setCoordinates(data.content);
        } catch (error) {
            console.error(error);
        }
    };

    const fetchPersons = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/persons", {
                headers: {Authorization: `Bearer ${token}`},
            });
            if (!response.ok) throw new Error("Ошибка загрузки администраторов");
            const data = await response.json();
            setPersons(data.content);
        } catch (error) {
            console.error(error);
        }
    };

    const fetchLocations = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/locations", {
                headers: {Authorization: `Bearer ${token}`},
            });
            if (!response.ok) throw new Error("Ошибка загрузки локаций");
            const data = await response.json();
            setLocations(data.content);
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        fetchGroups();
        fetchCoordinates();
        fetchPersons();
        fetchLocations();
    }, []);

    // Обработчик открытия модального окна для редактирования
    const handleEditGroup = (group) => {
        setEditingGroup(group);
        setIsGroupModalOpen(true);
    };

    // Обработчик открытия модального окна для создания новой группы
    const handleCreateGroup = () => {
        setEditingGroup(null);
        setIsGroupModalOpen(true);
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">All Groups</h1>
                <div className="flex space-x-4">
                    <button
                        className="px-4 py-2 bg-indigo-500 text-white font-medium rounded hover:bg-indigo-600"
                        onClick={handleCreateGroup}
                    >
                        Create New Group
                    </button>
                    <button
                        className="px-4 py-2 bg-green-500 text-white font-medium rounded hover:bg-green-600 transition duration-200"
                        onClick={() => setIsPersonModalOpen(true)}
                    >
                        Create New Person
                    </button>
                    <button
                        className="px-4 py-2 bg-blue-500 text-white font-medium rounded hover:bg-blue-600 transition duration-200"
                        onClick={() => setIsCoordinatesModalOpen(true)}
                    >
                        Create New Coordinates
                    </button>
                    <button
                        className="px-4 py-2 bg-purple-500 text-white font-medium rounded hover:bg-purple-600 transition duration-200"
                        onClick={() => setIsLocationModalOpen(true)}
                    >
                        Create New Location
                    </button>
                </div>
            </div>


            <div className="overflow-x-auto">
                <table className="min-w-full bg-white border border-gray-300 rounded shadow">
                    <thead>
                    <tr>
                        <th className="border px-4 py-2">ID</th>
                        <th className="border px-4 py-2">Name</th>
                        <th className="border px-4 py-2">Students Count</th>
                        <th className="border px-4 py-2">Coordinates</th>
                        <th className="border px-4 py-2">Admin Name</th>
                        <th className="border px-4 py-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {groups.map((group) => (
                        <tr key={group.id} className="hover:bg-gray-100">
                            <td className="border px-4 py-2">{group.id}</td>
                            <td className="border px-4 py-2">{group.name}</td>
                            <td className="border px-4 py-2">{group.studentsCount}</td>
                            <td className="border px-4 py-2">
                            {group.coordinates
                                    ? `X: ${group.coordinates.x}, Y: ${group.coordinates.y}`
                                    : "N/A"}
                            </td>
                            <td className="border px-4 py-2">
                                {group.groupAdmin?.name || "N/A"}
                            </td>
                            <td className="border px-4 py-2">
                                <button
                                    className="px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                                    onClick={() => console.log("Delete")}
                                >
                                    Delete
                                </button>
                                <button
                                    className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                                    onClick={() => handleEditGroup(group)}
                                >
                                    Edit
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <GroupModal
                isOpen={isGroupModalOpen}
                onRequestClose={() => setIsGroupModalOpen(false)}
                group={editingGroup}
                coordinates={coordinates}
                persons={persons}
                locations={locations}
            />
            {/* Модальное окно для Person */}
            <PersonModal
                isOpen={isPersonModalOpen}
                onRequestClose={() => setIsPersonModalOpen(false)}
                locations={locations}
            />

            {/* Модальное окно для Coordinates */}
            <CoordinatesModal
                isOpen={isCoordinatesModalOpen}
                onRequestClose={() => setIsCoordinatesModalOpen(false)}
            />

            {/* Модальное окно для Location */}
            <LocationModal
                isOpen={isLocationModalOpen}
                onRequestClose={() => setIsLocationModalOpen(false)}
            />
        </div>
    );
}
