import { useState, useEffect } from "react";
import GroupModal from "../../components/modal_windows/GroupModal.jsx";
import PersonModal from "../../components/modal_windows/PersonModal.jsx";
import CoordinatesModal from "../../components/modal_windows/CoordinatesModal.jsx";
import LocationModal from "../../components/modal_windows/LocationModal.jsx";

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

    // Загружаем группы с сервера
    const fetchGroups = async (page = 0) => {
        try {
            const response = await fetch(
                `http://localhost:8080/study-groups?page=${page}&size=10`, // Удален параметр сортировки
                { headers: { Authorization: `Bearer ${token}` } }
            );
            if (!response.ok) {
                console.log("Ошибка загрузки групп");
                throw new Error("Ошибка загрузки групп");
            }
            const data = await response.json();
            setGroups(data.content); // Устанавливаем полученные группы

            setTotalPages(data.totalPages); // Устанавливаем общее количество страниц
            setCurrentPage(data.number); // Устанавливаем текущую страницу
        } catch (error) {
            console.error(error);
        }
    };

    console.log(groups)

    // Обработчик открытия модального окна для редактирования
    const handleEditGroup = (group) => {
        setEditGroup(true);
        setEditingGroup(group);
        setIsGroupModalOpen(true);
    };

    // Обработчик открытия модального окна для создания новой группы
    const handleCreateGroup = () => {
        setEditingGroup(null);
        setIsGroupModalOpen(true);
    };

    useEffect(() => {
        fetchGroups();
    }, []);

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

    const handleDeleteGroup = async (groupId) => {
        if (!window.confirm("Are you sure you want to delete this group?")) {
            return; // Пользователь отменил удаление
        }

        console.log(groupId);

        try {
            const response = await fetch(`http://localhost:8080/study-groups/${groupId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error(`Failed to delete group: ${response.statusText}`);
            }
        } catch (error) {
            console.error("Failed to delete group:", error);
            alert("Failed to delete group. Please try again.");
        }
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

            {/* Таблица с группами */}
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white border border-gray-300 rounded shadow">
                    <thead>
                    <tr>
                        <th className="border px-4 py-2">ID</th>
                        <th className="border px-4 py-2">Name</th>
                        <th className="border px-4 py-2">Coordinate X</th>
                        <th className="border px-4 py-2">Coordinate Y</th>
                        <th className="border px-4 py-2">Creation Date</th>
                        <th className="border px-4 py-2">Students Count</th>
                        <th className="border px-4 py-2">Expelled Students</th>
                        <th className="border px-4 py-2">Transferred Students</th>
                        <th className="border px-4 py-2">Form of Education</th>
                        <th className="border px-4 py-2">Should Be Expelled</th>
                        <th className="border px-4 py-2">Semester</th>
                        <th className="border px-4 py-2">Admin Name</th>
                        <th className="border px-4 py-2">Admin Eye Color</th>
                        <th className="border px-4 py-2">Admin Hair Color</th>
                        <th className="border px-4 py-2">Admin Location X</th>
                        <th className="border px-4 py-2">Admin Location Y</th>
                        <th className="border px-4 py-2">Admin Location Name</th>
                        <th className="border px-4 py-2">Admin Weight</th>
                        <th className="border px-4 py-2">Admin Nationality</th>
                        <th className="border px-4 py-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {groups.map((group) => (
                        <tr key={group.id} className="hover:bg-gray-100">
                            <td className="border px-4 py-2">{group.id}</td>
                            <td className="border px-4 py-2">{group.name}</td>
                            <td className="border px-4 py-2">{group.coordinates?.x || "N/A"}</td>
                            <td className="border px-4 py-2">{group.coordinates?.y || "N/A"}</td>
                            <td className="border px-4 py-2">{group.creationDate || "N/A"}</td>
                            <td className="border px-4 py-2">{group.studentsCount}</td>
                            <td className="border px-4 py-2">{group.expelledStudents}</td>
                            <td className="border px-4 py-2">{group.transferredStudents || "N/A"}</td>
                            <td className="border px-4 py-2">{group.formOfEducation || "N/A"}</td>
                            <td className="border px-4 py-2">{group.shouldBeExpelled}</td>
                            <td className="border px-4 py-2">{group.semesterEnum || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.name || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.eyeColor || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.hairColor || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.location?.x || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.location?.y || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.location?.name || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.weight || "N/A"}</td>
                            <td className="border px-4 py-2">{group.groupAdmin?.nationality || "N/A"}</td>
                            <td className="border px-4 py-2">
                                <div className="flex space-x-2">
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
            <div className="flex justify-between items-center mt-4">
                <button
                    className={`px-4 py-2 bg-gray-300 text-gray-700 rounded ${
                        currentPage === 0 ? "cursor-not-allowed opacity-50" : ""
                    }`}
                    onClick={handlePreviousPage}
                    disabled={currentPage === 0}
                >
                    Previous
                </button>
                <span className="text-gray-700">
                    Page {currentPage + 1} of {totalPages}
                </span>
                <button
                    className={`px-4 py-2 bg-gray-300 text-gray-700 rounded ${
                        currentPage === totalPages - 1 ? "cursor-not-allowed opacity-50" : ""
                    }`}
                    onClick={handleNextPage}
                    disabled={currentPage === totalPages - 1}
                >
                    Next
                </button>
            </div>

            {/* Модальные окна */}
            <GroupModal
                isOpen={isGroupModalOpen}
                onRequestClose={() => {setIsGroupModalOpen(false);
                                      setEditGroup(false);}}
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
        </div>
    );
}
