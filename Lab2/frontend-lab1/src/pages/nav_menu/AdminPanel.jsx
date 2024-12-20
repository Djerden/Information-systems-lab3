import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";


export default function AdminPanel() {
    const [requests, setRequests] = useState([]); // List of requests
    const [loading, setLoading] = useState(true); // Loading state
    const [error, setError] = useState(null); // Error state

    // Pagination, sorting, and filtering states
    const [sortBy, setSortBy] = useState("createdAt");
    const [sortDirection, setSortDirection] = useState("desc");
    const [statusFilter, setStatusFilter] = useState(""); // "" means no filter
    const [page, setPage] = useState(0); // Current page
    const [size, setSize] = useState(10); // Page size
    const [totalPages, setTotalPages] = useState(1); // Total number of pages

    // WebSocket client
    useEffect(() => {
        const client = new Client({
            brokerURL: "ws://localhost:8080/ws", // URL для подключения WebSocket
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("WebSocket connected");
                // Подписка на канал для получения обновлений заявок
                client.subscribe("/topic/admin-request", (message) => {
                    // Когда получаем обновление, обновляем данные
                    console.log("Received data:", message.body); // Логируем полученные данные
                    fetchRequests(); // Обновляем список заявок с сервера
                });
            },
            onStompError: (frame) => {
                console.error("STOMP error: " + frame);
            },
        });

        client.activate(); // Активация WebSocket клиента

        // Очистка при размонтировании компонента
        return () => {
            client.deactivate();
        };
    }, [sortBy, sortDirection, statusFilter, page, size]); // Подписка на обновления при изменении токена

    // Fetch requests with sorting and filtering
    const fetchRequests = async () => {
        setLoading(true);
        setError(null);
        try {
            const token = sessionStorage.getItem("jwt");
            const url = new URL("http://localhost:8080/admin");
            url.searchParams.append("page", page);
            url.searchParams.append("size", size);
            url.searchParams.append("sortBy", sortBy);
            url.searchParams.append("sortDirection", sortDirection);
            if (statusFilter) {
                url.pathname = "/admin/filter";
                url.searchParams.append("status", statusFilter);
            }

            const response = await fetch(url.toString(), {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to load requests");
            }

            const data = await response.json();
            setRequests(data.content || data); // If paginated, data.content is used
            setTotalPages(data.totalPages || 1); // Set total pages from response
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const handleSortChange = (e) => {
        const [field, direction] = e.target.value.split(":");
        setSortBy(field);
        setSortDirection(direction);
    };

    const handleStatusChange = (e) => {
        setStatusFilter(e.target.value);
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            setPage(newPage);
        }
    };

    // Fetch requests when dependencies change
    useEffect(() => {
        fetchRequests();
    }, [sortBy, sortDirection, statusFilter, page]);

    const approveRequest = async (requestId) => {
        try {
            const token = sessionStorage.getItem("jwt");
            const response = await fetch(`http://localhost:8080/admin/${requestId}/approve`, {
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to approve request");
            }

            // alert("Request approved successfully");
            // fetchRequests(); // Refresh the list
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    };

    const rejectRequest = async (requestId) => {
        try {
            const token = sessionStorage.getItem("jwt");
            const response = await fetch(`http://localhost:8080/admin/${requestId}/reject`, {
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to reject request");
            }

            // alert("Request rejected successfully");
            // fetchRequests(); // Refresh the list
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Admin Panel</h1>

            {/* Filters and Sorting */}
            <div className="mb-4 flex items-center space-x-4">
                {/* Status Filter */}
                <select
                    value={statusFilter}
                    onChange={handleStatusChange}
                    className="px-4 py-2 border border-gray-300 rounded"
                >
                    <option value="">All Statuses</option>
                    <option value="PENDING">Pending</option>
                    <option value="APPROVED">Approved</option>
                    <option value="REJECTED">Rejected</option>
                </select>

                {/* Sort Options */}
                <select
                    value={`${sortBy}:${sortDirection}`}
                    onChange={handleSortChange}
                    className="px-4 py-2 border border-gray-300 rounded"
                >
                    <option value="createdAt:desc">Newest First</option>
                    <option value="createdAt:asc">Oldest First</option>
                </select>
                {/* Reset Button */}
                <button
                    onClick={() => {
                        setStatusFilter(""); // сбросить фильтр статуса
                        setSortBy("createdAt"); // сбросить сортировку по умолчанию
                        setSortDirection("desc"); // сбросить сортировку по умолчанию
                    }}
                    className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                >
                    Reset Filters
                </button>
            </div>

            {/* Table of Requests */}
            {loading && <p>Loading requests...</p>}
            {error && <p className="text-red-500">Error: {error}</p>}
            {!loading && !error && (
                <div>
                    <div className="overflow-x-auto">
                        <table className="table-auto w-full border-collapse border border-gray-300">
                            <thead>
                            <tr>
                                <th className="border border-gray-300 px-4 py-2">ID</th>
                                <th className="border border-gray-300 px-4 py-2">User name</th>
                                <th className="border border-gray-300 px-4 py-2">Status</th>
                                <th className="border border-gray-300 px-4 py-2">Creation Date</th>
                                <th className="border border-gray-300 px-4 py-2">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {requests.map((request) => (
                                <tr key={request.id} className="hover:bg-gray-100">
                                    <td className="border border-gray-300 px-4 py-2">{request.id}</td>
                                    <td className="border border-gray-300 px-4 py-2">{request.user.username}</td>
                                    <td className="border border-gray-300 px-4 py-2">{request.status}</td>
                                    <td className="border border-gray-300 px-4 py-2">
                                        {new Date(request.createdAt).toLocaleString("ru-RU")}
                                    </td>
                                    <td className="border border-gray-300 px-4 py-2">
                                        {request.status === "PENDING" ? (
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => approveRequest(request.id)}
                                                    className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition duration-200"
                                                >
                                                    Approve
                                                </button>
                                                <button
                                                    onClick={() => rejectRequest(request.id)}
                                                    className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition duration-200"
                                                >
                                                    Reject
                                                </button>
                                            </div>
                                        ) : (
                                            <span>No actions available</span>
                                        )}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Pagination Controls */}
                    <div className="mt-4 flex justify-between items-center">
                        <button
                            onClick={() => handlePageChange(page - 1)}
                            disabled={page === 0}
                            className={`px-4 py-2 rounded ${
                                page === 0 ? "bg-gray-300 cursor-not-allowed" : "bg-indigo-500 text-white hover:bg-indigo-600"
                            }`}
                        >
                            Previous
                        </button>
                        <span>
                            Page {page + 1} of {totalPages}
                        </span>
                        <button
                            onClick={() => handlePageChange(page + 1)}
                            disabled={page + 1 >= totalPages}
                            className={`px-4 py-2 rounded ${
                                page + 1 >= totalPages
                                    ? "bg-gray-300 cursor-not-allowed"
                                    : "bg-indigo-500 text-white hover:bg-indigo-600"
                            }`}
                        >
                            Next
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
