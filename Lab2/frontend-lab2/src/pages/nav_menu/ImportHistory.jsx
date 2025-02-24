    import React, { useEffect, useState } from "react";
import ImportFilesModal from "../../components/modal_windows/ImportFileModal.jsx";
import { Client } from "@stomp/stompjs";
    import {FaDownload} from "react-icons/fa";

export default function ImportHistory() {
    const [isImportModalOpen, setIsImportModalOpen] = useState(false);
    const [importHistory, setImportHistory] = useState([]);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [sortBy, setSortBy] = useState("timestamp");
    const [direction, setDirection] = useState("DESC");
    const [status, setStatus] = useState(""); // Фильтр по статусу
    const [totalPages, setTotalPages] = useState(0);

    const token = sessionStorage.getItem("jwt");
    const role = token ? JSON.parse(atob(token.split(".")[1]))?.role : "";
    console.log(token)
    const fetchHistory = async () => {
        try {
            const url =
                role === "ROLE_ADMIN"
                    ? `http://localhost:8080/import/history/admin?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`
                    : `http://localhost:8080/import/history/user?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`;

            const response = await fetch(url, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            const data = await response.json();
            setImportHistory(data.content || []);
            setTotalPages(data.totalPages || 1);
        } catch (error) {
            console.error("Failed to fetch import history:", error);
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            setPage(newPage);
        }
    };
    console.log(importHistory)
    // WebSocket client
    useEffect(() => {
        const client = new Client({
            brokerURL: "ws://localhost:8080/ws",
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("WebSocket connected");
                client.subscribe("/topic/import-history", (message) => {
                    console.log("Received WebSocket message:", message.body);
                    fetchHistory();
                });
            },
            onStompError: (frame) => {
                console.error("STOMP error:", frame);
            },
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, []);

    // Fetch history when dependencies change
    useEffect(() => {
        fetchHistory();
    }, [page, size, sortBy, direction, status]);


    const handleDownloadFile = async (fileUrl, fileName) => {
        try {
            const response = await fetch(`http://localhost:8080/import/download/${fileUrl}`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                const blob = await response.blob();
                const link = document.createElement("a");
                link.href = URL.createObjectURL(blob);
                link.download = fileName;
                link.click();
            } else {
                console.error("Ошибка при скачивании файла");
            }
        } catch (error) {
            console.error("Не удалось скачать файл:", error);
        }
    };

    return (
        <div className="container mx-auto p-4">
            <div className="text-left mb-4">
                <button
                    className="px-4 py-2 bg-orange-500 text-white font-medium rounded hover:bg-orange-600 transition duration-200"
                    onClick={() => setIsImportModalOpen(true)}
                >
                    Import File
                </button>
            </div>

            <table className="table-auto w-full border-collapse border border-gray-300 mt-4">
                <thead>
                <tr>
                    <th className="border border-gray-300 px-4 py-2">Operation ID</th>
                    <th className="border border-gray-300 px-4 py-2">User</th>
                    <th className="border border-gray-300 px-4 py-2">Status</th>
                    <th className="border border-gray-300 px-4 py-2">Date</th>
                    <th className="border border-gray-300 px-4 py-2">Added Objects</th>
                    <th className="border border-gray-300 px-4 py-2">File Name</th>
                    <th className="border border-gray-300 px-4 py-2">Download</th>
                </tr>
                </thead>
                <tbody>
                {importHistory.map((operation) => (
                    <tr key={operation.id} className="hover:bg-gray-100">
                        <td className="border border-gray-300 px-4 py-2">{operation.id}</td>
                        <td className="border border-gray-300 px-4 py-2">{operation.user.username}</td>
                        <td className="border border-gray-300 px-4 py-2">{operation.status}</td>
                        <td className="border border-gray-300 px-4 py-2">
                            {new Date(operation.timestamp).toLocaleString("ru-RU")}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                            {operation.status === "SUCCESS" ? operation.addedObjects : "-"}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">{operation.fileName}</td>
                        <td className="border border-gray-300 px-4 py-2">
                            {operation.fileUrl && (
                                <button
                                    className="text-blue-500 hover:underline"
                                    onClick={() => handleDownloadFile(operation.fileUrl, operation.fileName)}
                                >
                                    <FaDownload />
                                </button>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <div className="flex justify-between items-center mt-4">
                <button
                    className="px-4 py-2 bg-indigo-500 text-white rounded"
                    onClick={() => handlePageChange(page - 1)}
                    disabled={page === 0}
                >
                    Previous
                </button>
                <span>{`Page ${page + 1} of ${totalPages}`}</span>
                <button
                    className="px-4 py-2 bg-indigo-500 text-white rounded"
                    onClick={() => handlePageChange(page + 1)}
                    disabled={page === totalPages - 1}
                >
                    Next
                </button>
            </div>

            <ImportFilesModal
                isOpen={isImportModalOpen}
                onRequestClose={() => setIsImportModalOpen(false)}
                onFilesImported={fetchHistory}
            />
        </div>
    );
}