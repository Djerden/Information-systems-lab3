import {useState, useEffect} from "react";
import GroupModal from "../../components/modal_windows/GroupModal.jsx";
import PersonModal from "../../components/modal_windows/PersonModal.jsx";
import CoordinatesModal from "../../components/modal_windows/CoordinatesModal.jsx";
import LocationModal from "../../components/modal_windows/LocationModal.jsx";
import GroupHistoryModal from "../../components/modal_windows/GroupHistoryModal.jsx";

import { Client } from "@stomp/stompjs";  // импортируем клиента для WebSocket


export default function Groups() {
    const [groups, setGroups] = useState([]);
    const [editingGroup, setEditingGroup] = useState(null);

    const token = sessionStorage.getItem("jwt");

    // Константы модальных окон
    const [isGroupModalOpen, setIsGroupModalOpen] = useState(false);
    const [isPersonModalOpen, setIsPersonModalOpen] = useState(false);
    const [isCoordinatesModalOpen, setIsCoordinatesModalOpen] = useState(false);
    const [isLocationModalOpen, setIsLocationModalOpen] = useState(false);

    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);

    const [editGroup, setEditGroup] = useState(false);

    // константы для истории StudyGroup
    const [selectedGroup, setSelectedGroup] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Фильтрация и сортировка
    const [filterName, setFilterName] = useState("");
    const [filterFormOfEducation, setFilterFormOfEducation] = useState("");
    const [filterSemester, setFilterSemester] = useState("");
    const [filterAdminName, setFilterAdminName] = useState("");
    const [sortBy, setSortBy] = useState("creationDate");
    const [sortDirection, setSortDirection] = useState("desc");

    // Загружаем группы с сервера
    const fetchGroups = async (page = 0) => {
        try {
            const params = new URLSearchParams({
                page,
                size: 7,
                sortBy,
                sortDirection,
            });

            // Добавляем параметры только если они не пустые
            if (filterName) params.append("name", filterName);
            if (filterFormOfEducation) params.append("formOfEducation", filterFormOfEducation);
            if (filterSemester) params.append("semesterEnum", filterSemester);
            if (filterAdminName) params.append("adminName", filterAdminName);

            console.log(`Запрос к серверу: http://localhost:8080/study-groups/filter?${params.toString()}`);

            const response = await fetch(`http://localhost:8080/study-groups/filter?${params.toString()}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!response.ok) {
                throw new Error("Ошибка загрузки групп");
            }

            const data = await response.json();
            console.log("Данные получены: ", data);

            setGroups(data.content);
            setTotalPages(data.totalPages);
            setCurrentPage(data.number);
        } catch (error) {
            console.error("Ошибка во время загрузки данных: ", error);
        }
    };

    console.log('Страница = ' + currentPage)

    useEffect(() => {
        fetchGroups();
    }, [sortBy, sortDirection, filterName, filterFormOfEducation, filterSemester, filterAdminName]);

    useEffect(() => {
        const client = new Client({
            brokerURL: "ws://localhost:8080/ws", // URL для подключения WebSocket
            // connectHeaders: {
            //     Authorization: `Bearer ${token}`, // отправляем токен для авторизации
            // },
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("WebSocket connected");
                // Подписка на канал для уведомлений по обновлениям группы
                client.subscribe("/topic/study-groups", (message) => {
                    // Когда уведомление приходит, обновляем данные
                    console.log("Received data:", message.body); // Логируем полученные данные

                    console.log(currentPage)
                    console.log(sortBy)
                    console.log(sortDirection)
                    console.log(filterName)
                    console.log(filterSemester)
                    console.log(filterFormOfEducation)
                    console.log(filterAdminName)
                    fetchGroups(currentPage); // Обновляем данные с сервера (текущая страница будет автоматически учитываться)
                });
            },
            onStompError: (frame) => {
                console.error("STOMP error: " + frame);
            },
        });

        client.activate(); // Активация клиента

        // Очистка при размонтировании компонента
        return () => {
            client.deactivate();
        };
    }, [currentPage, sortBy, sortDirection, filterName, filterFormOfEducation, filterSemester, filterAdminName]); // Подписка на уведомления при изменении токена (не зависит от страницы)


    // Обработчик открытия модального окна для создания новой группы
    const handleCreateGroup = () => {
        setEditingGroup(null);
        setIsGroupModalOpen(true);
    };

    // Переход на следующую страницу
    const handleNextPage = () => {
        if (currentPage < totalPages - 1) {
            fetchGroups(currentPage + 1);
        }
    };

    // Переход на предыдущую страницу
    const handlePreviousPage = () => {
        if (currentPage > 0) {
            fetchGroups(currentPage - 1);
        }
    };

    const handleViewDetails = (group) => {
        setSelectedGroup(group); // Устанавливаем выбранный объект
        setIsModalOpen(true); // Открываем модальное окно
    };

    // Обработчик открытия модального окна для редактирования
    const handleEditGroup = (group) => {
        setEditGroup(true);
        setEditingGroup(group);
        setIsGroupModalOpen(true);
    };

    const handleDeleteGroup = async (groupId) => {
        if (!window.confirm("Are you sure you want to delete this group?")) {
            return; // Пользователь отменил удаление
        }

        try {
            const response = await fetch(`http://localhost:8080/study-groups/${groupId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                if (response.status === 403) {
                    // Если статус 403 (Forbidden), показываем "Access Denied"
                    alert("Access denied, you do not have the rights to delete this group");
                } else {
                    throw new Error(`Failed to delete group: ${response.statusText}`);
                }
            }
        } catch (error) {
            console.error("Failed to delete group:", error);
            alert("Failed to delete group. Please try again.");
        }
    };

    const formatEducation = (education) => {
        if (!education) return "N/A"; // Если значение отсутствует
        return education.replace(/_/g, " "); // Заменяем все символы "_" на пробел
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

            {/* Панель фильтрации и сортировки */}
            <div className="grid grid-cols-3 gap-4 mb-4">
                <div>
                    <label className="block font-medium mb-1">Filter by Name:</label>
                    <input
                        type="text"
                        value={filterName}
                        onChange={(e) => setFilterName(e.target.value)}
                        className="w-full px-4 py-2 border rounded"
                    />
                </div>
                <div>
                    <label className="block font-medium mb-1">Filter by Form of Education:</label>
                    <select
                        value={filterFormOfEducation}
                        onChange={(e) => setFilterFormOfEducation(e.target.value)}
                        className="w-full px-4 py-2 border rounded"
                    >
                        <option value="">All</option>
                        <option value="FULL_TIME_EDUCATION">Full Time Education</option>
                        <option value="DISTANCE_EDUCATION">Distance Education</option>
                        <option value="EVENING_CLASSES">Evening Classes</option>
                    </select>
                </div>
                <div>
                    <label className="block font-medium mb-1">Sort By:</label>
                    <select
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value)}
                        className="w-full px-4 py-2 border rounded"
                    >
                        <option value="creationDate">Creation Date</option>
                        <option value="name">Name</option>
                        <option value="studentsCount">Students Count</option>
                        <option value="transferredStudents">Transferred Students</option>
                        <option value="shouldBeExpelled">Should Be Expelled</option>
                    </select>
                </div>
                <div>
                    <label className="block font-medium mb-1">Filter by Admin Name:</label>
                    <input
                        type="text"
                        value={filterAdminName}
                        onChange={(e) => setFilterAdminName(e.target.value)}
                        className="w-full px-4 py-2 border rounded"
                    />
                </div>
                <div>
                    <label className="block font-medium mb-1">Filter by Semester:</label>
                    <select
                        value={filterSemester}
                        onChange={(e) => setFilterSemester(e.target.value)}
                        className="w-full px-4 py-2 border rounded"
                    >
                        <option value="">All</option>
                        <option value="FIRST">First</option>
                        <option value="THIRD">Third</option>
                        <option value="FIFTH">Fifth</option>
                        <option value="SIXTH">Sixth</option>
                        <option value="SEVENTH">Seventh</option>
                    </select>
                </div>
                <div>
                    <label className="block font-medium mb-1">Sort Direction:</label>
                    <select
                        value={sortDirection}
                        onChange={(e) => setSortDirection(e.target.value)}
                        className="w-full px-4 py-2 border rounded"
                    >
                        <option value="asc">Ascending</option>
                        <option value="desc">Descending</option>
                    </select>
                </div>
                {/* Кнопка сброса фильтров */}
                <div className="col-span-3">
                    <button
                        onClick={() => {
                            setFilterName("");
                            setFilterFormOfEducation("");
                            setFilterSemester("");
                            setFilterAdminName("");
                            setSortBy("creationDate");
                            setSortDirection("desc");
                            fetchGroups(); // Обновляем данные после сброса
                        }}
                        className="w-full px-4 py-1 bg-gray-200 text-gray-700 border border-gray-300 rounded
                   hover:bg-red-500 hover:text-white hover:border-red-600 transition duration-200"
                    >
                        Reset Filters
                    </button>
                </div>
            </div>

            {/* Таблица с группами */}
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white border border-gray-300 rounded shadow">
                    <thead>
                    <tr>
                        <th className="border px-4 py-2">ID</th>
                        <th className="border px-4 py-2">Name</th>
                        <th className="border px-4 py-2">Coordinates</th>
                        <th className="border px-4 py-2">Creation Date</th>
                        <th className="border px-4 py-2">Students Count</th>
                        <th className="border px-4 py-2">Expelled Students</th>
                        <th className="border px-4 py-2">Transferred Students</th>
                        <th className="border px-4 py-2">Form of Education</th>
                        <th className="border px-4 py-2">Should Be Expelled</th>
                        <th className="border px-4 py-2">Semester</th>
                        <th className="border px-4 py-2">Admin Name</th>
                        <th className="border px-4 py-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {groups.map((group) => (
                        <tr key={group.id} className="hover:bg-gray-100">
                            <td className="border px-4 py-2">{group.id}</td>
                            <td className="border px-4 py-2">{group.name}</td>
                            <td className="border px-4 py-2">
                                X: {group.coordinates.x}
                                <br/>
                                Y: {group.coordinates.y}
                            </td>
                            <td className="border px-4 py-2">{group.creationDate || "N/A"}</td>
                            <td className="border px-4 py-2">{group.studentsCount}</td>
                            <td className="border px-4 py-2">{group.expelledStudents}</td>
                            <td className="border px-4 py-2">{group.transferredStudents || "N/A"}</td>
                            <td className="border px-4 py-2">{formatEducation(group.formOfEducation)}</td>
                            <td className="border px-4 py-2">{group.shouldBeExpelled}</td>
                            <td className="border px-4 py-2">{group.semesterEnum || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.name || "N/A"}</td>
                            <td className="border px-4 py-2">
                                <div className="flex space-x-2">
                                    <button
                                        className="w-24 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 text-center"
                                        onClick={() => handleViewDetails(group)}
                                    >
                                        Details
                                    </button>
                                    <button
                                        className="w-24 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 text-center"
                                        onClick={() => handleEditGroup(group)}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        className="w-24 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 text-center"
                                        onClick={() => handleDeleteGroup(group.id)}
                                    >
                                        Delete
                                    </button>

                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {/* Навигация по страницам */}
            <div className="mt-4 flex justify-between items-center">
                <button
                    onClick={handlePreviousPage}
                    disabled={currentPage === 0}
                    className={`px-4 py-2 rounded ${
                        currentPage === 0
                            ? "bg-gray-300 cursor-not-allowed"
                            : "bg-indigo-500 text-white hover:bg-indigo-600"
                    }`}
                >
                    Previous
                </button>
                <span>
                    Page {currentPage + 1} of {totalPages}
                </span>
                <button
                    onClick={handleNextPage}
                    disabled={currentPage + 1 >= totalPages}
                    className={`px-4 py-2 rounded ${
                        currentPage + 1 >= totalPages
                            ? "bg-gray-300 cursor-not-allowed"
                            : "bg-indigo-500 text-white hover:bg-indigo-600"
                    }`}
                >
                    Next
                </button>
            </div>

            {/* Модальные окна */}
            <GroupModal
                isOpen={isGroupModalOpen}
                onRequestClose={() => {
                    setIsGroupModalOpen(false);
                    setEditGroup(false);
                }}
                group={editingGroup}
                onSave={fetchGroups} // Перезагрузка групп после сохранения
                editGroup={editGroup}
            />
            <PersonModal
                isOpen={isPersonModalOpen}
                onRequestClose={() => setIsPersonModalOpen(false)}
            />
            <CoordinatesModal
                isOpen={isCoordinatesModalOpen}
                onRequestClose={() => setIsCoordinatesModalOpen(false)}
            />
            <LocationModal
                isOpen={isLocationModalOpen}
                onRequestClose={() => setIsLocationModalOpen(false)}
            />

            <GroupHistoryModal
                isOpen={isModalOpen}
                onRequestClose={() => setIsModalOpen(false)}
                group={selectedGroup}
            />
        </div>
    );
}
