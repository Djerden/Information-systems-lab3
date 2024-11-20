import { useEffect, useState } from "react";

export default function AdminPanel() {
    const [requests, setRequests] = useState([]); // Список заявок
    const [loading, setLoading] = useState(true); // Состояние загрузки
    const [error, setError] = useState(null); // Состояние ошибок

    // Функция для получения всех заявок
    const fetchRequests = async () => {
        setLoading(true);
        setError(null);
        try {
            const token = sessionStorage.getItem("jwt");
            const response = await fetch("http://localhost:8080/admin", {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Не удалось загрузить заявки");
            }

            const data = await response.json();
            setRequests(data);
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    // Функция для одобрения заявки
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
                throw new Error(errorData.message || "Не удалось одобрить заявку");
            }

            alert("Заявка успешно одобрена");
            fetchRequests(); // Обновляем список заявок
        } catch (error) {
            alert(`Ошибка: ${error.message}`);
        }
    };

    // Функция для отклонения заявки
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
                throw new Error(errorData.message || "Не удалось отклонить заявку");
            }

            alert("Заявка успешно отклонена");
            fetchRequests(); // Обновляем список заявок
        } catch (error) {
            alert(`Ошибка: ${error.message}`);
        }
    };

    // Получаем заявки при загрузке компонента
    useEffect(() => {
        fetchRequests();
    }, []);

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Admin Panel</h1>

            {loading && <p>Загрузка заявок...</p>}
            {error && <p className="text-red-500">Ошибка: {error}</p>}

            {!loading && !error && (
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
                            <tr key={request.id}>
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
                                        <span>There are no actions</span>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
