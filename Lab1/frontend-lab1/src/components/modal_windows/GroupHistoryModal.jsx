import React, { useEffect, useState } from "react";
import Modal from "react-modal";

Modal.setAppElement("#root");

export default function GroupDetailsModal({ isOpen, onRequestClose, group }) {
    const [history, setHistory] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (group && isOpen) {
            const fetchHistory = async () => {
                try {
                    const response = await fetch(
                        `http://localhost:8080/study-groups/${group.id}/history`,
                        {
                            method: "GET",
                            headers: {
                                "Content-Type": "application/json",
                                Authorization: `Bearer ${sessionStorage.getItem("jwt")}`,
                            },
                        }
                    );

                    if (response.ok) {
                        const data = await response.json();
                        setHistory(data);
                    } else {
                        console.error("Failed to fetch history");
                    }
                } catch (error) {
                    console.error("Error fetching history:", error);
                } finally {
                    setIsLoading(false);
                }
            };

            fetchHistory();
        }
    }, [group, isOpen]);

    // Функция для обработки значений
    const formatValue = (key, value) => {
        if (!value) return "N/A";

        if (key === "coordinates") {
            return (
                <span>
                <strong>X:</strong> {value.x}, <strong>Y:</strong> {value.y}
            </span>
            );
        } else if (key === "groupAdmin") {
            return (
                <span>
                <strong>Name:</strong> {value.name}, <strong>Eye Color:</strong> {value.eyeColor}, <strong>Hair Color:</strong> {value.hairColor}, <strong>Location:</strong> (X: {value.location?.x}, Y: {value.location?.y}, Name: {value.location?.name}), <strong>Weight:</strong> {value.weight}, <strong>Nationality:</strong> {value.nationality}
            </span>
            );
        } else if (key === "user") {
            return (
                <span>
                <strong>Username:</strong> {value.username}
            </span>
            );
        }

        // Для других вложенных объектов
        if (typeof value === "object") {
            return (
                <span>
                {Object.entries(value).map(([nestedKey, nestedValue], index) => (
                    <React.Fragment key={nestedKey}>
                        <strong>{nestedKey}:</strong> {nestedValue}
                        {index < Object.entries(value).length - 1 && ", "}
                    </React.Fragment>
                ))}
            </span>
            );
        }

        return <span>{value}</span>; // Для примитивных типов
    };



    const formatDateTime = (dateString) => {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0'); // Добавляем 0, если месяц состоит из одной цифры
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    };


    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onRequestClose}
            overlayClassName="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
            className="bg-white rounded shadow-lg w-3/4 max-h-[90vh] overflow-y-auto p-6"
        >
            <h2 className="text-lg font-bold mb-4">Group Details</h2>

            {/* Таблица с актуальными данными */}
            {group && (
                <div>
                    <h3 className="text-md font-semibold mb-2">Current Data:</h3>
                    <table className="min-w-full bg-white border border-gray-300 rounded shadow mb-4">
                        <thead>
                        <tr>
                            <th className="border px-4 py-2">Field</th>
                            <th className="border px-4 py-2">Value</th>
                        </tr>
                        </thead>
                        <tbody>
                        {Object.entries(group).map(([key, value]) => (
                            <tr key={key}>
                                <td className="border px-4 py-2">{key}</td>
                                <td className="border px-4 py-2">{formatValue(key, value)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Таблица с историей */}
            <div className="mt-6">
                <h3 className="text-md font-semibold mb-2">History:</h3>
                {isLoading ? (
                    <p>Loading history...</p>
                ) : history.length > 0 ? (
                    <table className="min-w-full bg-white border border-gray-300 rounded shadow">
                        <thead>
                        <tr>
                            <th className="border px-4 py-2">Version</th>
                            <th className="border px-4 py-2">Updated At</th>
                            <th className="border px-4 py-2">Updated By</th>
                            <th className="border px-4 py-2">Field</th>
                            <th className="border px-4 py-2">Value</th>
                        </tr>
                        </thead>
                        <tbody>
                        {history.map((record, index) => (
                            <React.Fragment key={index}>
                                {/*Разделитель между записями*/}
                                {index > 0 && (
                                    <tr>
                                        <td colSpan="5" className="border-0 border-t border-b border-gray-300 border-dashed"></td>
                                    </tr>
                                )}

                                <tr className="bg-gray-100">
                                    <td className="border px-4 py-2" rowSpan="10">
                                        {record.version}
                                    </td>
                                    <td className="border px-4 py-2" rowSpan="10">
                                        {formatDateTime(record.updatedAt)}
                                    </td>
                                    <td className="border px-4 py-2" rowSpan="10">
                                        {record.updatedBy?.username || "N/A"}
                                    </td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Name</td>
                                    <td className="border px-4 py-2">{record.name}</td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Coordinates</td>
                                    <td className="border px-4 py-2">
                                        {formatValue("coordinates", record.coordinates)}
                                    </td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Students Count</td>
                                    <td className="border px-4 py-2">{record.studentsCount}</td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Expelled Students</td>
                                    <td className="border px-4 py-2">{record.expelledStudents}</td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Transferred Students</td>
                                    <td className="border px-4 py-2">{record.transferredStudents}</td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Form of Education</td>
                                    <td className="border px-4 py-2">{record.formOfEducation || "N/A"}</td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Should Be Expelled</td>
                                    <td className="border px-4 py-2">{record.shouldBeExpelled}</td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Semester</td>
                                    <td className="border px-4 py-2">{record.semesterEnum || "N/A"}</td>
                                </tr>
                                <tr>
                                    <td className="border px-4 py-2">Group Admin</td>
                                    <td className="border px-4 py-2">
                                        {formatValue("groupAdmin", record.groupAdmin)}
                                    </td>
                                </tr>
                            </React.Fragment>
                        ))}
                        </tbody>
                    </table>
                ) : (
                    <p>No history found.</p>
                )}
            </div>

            <button
                className="mt-4 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                onClick={onRequestClose}
            >
                Close
            </button>
        </Modal>
    );
}
