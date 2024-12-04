import { useState } from 'react';

export default function SpecialFunctions() {
    const [minExpelledResult, setMinExpelledResult] = useState(null);
    const [countResult, setCountResult] = useState(null);
    const [adminId, setAdminId] = useState('');
    const [groupIdForExpel, setGroupIdForExpel] = useState('');
    const [groupIdForAdd, setGroupIdForAdd] = useState('');
    const [actionMessage, setActionMessage] = useState('');

    // Ошибки для каждого поля
    const [adminIdError, setAdminIdError] = useState('');
    const [groupIdForExpelError, setGroupIdForExpelError] = useState('');
    const [groupIdForAddError, setGroupIdForAddError] = useState('');

    const token = sessionStorage.getItem('jwt');

    // Проверка на правильность ID (должен быть числом)
    const isValidNumber = (value) => !isNaN(value) && value.trim() !== '';

    const handleGetMinExpelled = async () => {
        const response = await fetch('http://localhost:8080/study-groups/min-expelled-students', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
        });
        const data = await response.json();
        setMinExpelledResult(data);
    };

    const handleCountByAdmin = async () => {
        setAdminIdError(''); // Сброс ошибки перед новым запросом

        if (!isValidNumber(adminId)) {
            setAdminIdError('Please enter a valid Admin ID.');
            return;
        }

        const response = await fetch(`http://localhost:8080/study-groups/count-by-admin?adminId=${adminId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
        });
        const data = await response.json();
        setCountResult(data);
    };

    const handleExpelAllStudents = async () => {
        setGroupIdForExpelError(''); // Сброс ошибки перед новым запросом

        if (!isValidNumber(groupIdForExpel)) {
            setGroupIdForExpelError('Please enter a valid Group ID for expulsion.');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/study-groups/${groupIdForExpel}/expel-students`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                setActionMessage(`Successfully expelled all students from group with ID: ${groupIdForExpel}`);
            } else {
                setActionMessage(`Failed to expel students. Please check the group ID.`);
            }
        } catch (error) {
            setActionMessage(`An error occurred: ${error.message}`);
        }
    };

    const handleAddStudentToGroup = async () => {
        setGroupIdForAddError(''); // Сброс ошибки перед новым запросом

        if (!isValidNumber(groupIdForAdd)) {
            setGroupIdForAddError('Please enter a valid Group ID to add a student.');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/study-groups/${groupIdForAdd}/add-student`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                setActionMessage(`Successfully added a student to group with ID: ${groupIdForAdd}`);
            } else {
                setActionMessage(`Failed to add student. Please check the group ID.`);
            }
        } catch (error) {
            setActionMessage(`An error occurred: ${error.message}`);
        }
    };

    return (
        <div className="p-6 max-w-2xl mx-auto bg-white rounded-lg shadow-lg">
            <h1 className="text-2xl font-bold mb-6">Special Functions</h1>

            <div className="mb-4">
                <h2 className="text-xl font-medium text-gray-600 mb-2">Get a group with minimal expelledStudents:</h2>
                <button
                    onClick={handleGetMinExpelled}
                    className="w-full p-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
                >
                    Get Group with Minimum Expelled Students
                </button>
                {minExpelledResult && (
                    <div className="mt-4 p-4 bg-gray-100 rounded">
                        <h3 className="font-semibold">Result:</h3>
                        <div className="space-y-2">
                            <div><strong>ID:</strong> {minExpelledResult.id}</div>
                            <div><strong>Name:</strong> {minExpelledResult.name}</div>
                            <div><strong>Expelled Students:</strong> {minExpelledResult.expelledStudents}</div>
                        </div>
                    </div>
                )}
            </div>

            <div className="mb-4">
                <h2 className="text-xl font-medium text-gray-600 mb-2">Get the number of groups whose adminId is greater than the specified one:</h2>
                <label htmlFor="adminId" className="block text-sm font-medium mb-2">Admin ID:</label>
                <input
                    id="adminId"
                    type="number"
                    value={adminId}
                    onChange={(e) => setAdminId(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-lg"
                />
                {adminIdError && <p className="text-red-500 text-sm">{adminIdError}</p>}
                <button
                    onClick={handleCountByAdmin}
                    className="w-full p-2 mt-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
                >
                    Count Groups with Admin ID Greater Than
                </button>
                {countResult !== null && (
                    <div className="mt-4 p-4 bg-gray-100 rounded">
                        <h3 className="font-semibold">Result:</h3>
                        <p>{countResult}</p>
                    </div>
                )}
            </div>

            <div className="mb-4">
                <h2 className="text-xl font-medium text-gray-600 mb-2">Expel all students from a group:</h2>
                <label htmlFor="groupId" className="block text-sm font-medium mb-2">Group ID:</label>
                <input
                    id="groupId"
                    type="number"
                    value={groupIdForExpel}
                    onChange={(e) => setGroupIdForExpel(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-lg"
                />
                {groupIdForExpelError && <p className="text-red-500 text-sm">{groupIdForExpelError}</p>}
                <button
                    onClick={handleExpelAllStudents}
                    className="w-full p-2 mt-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
                >
                    Expel All Students
                </button>
            </div>

            <div className="mb-4">
                <h2 className="text-xl font-medium text-gray-600 mb-2">Add a student to a group:</h2>
                <label htmlFor="groupIdAdd" className="block text-sm font-medium mb-2">Group ID:</label>
                <input
                    id="groupIdAdd"
                    type="number"
                    value={groupIdForAdd}
                    onChange={(e) => setGroupIdForAdd(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-lg"
                />
                {groupIdForAddError && <p className="text-red-500 text-sm">{groupIdForAddError}</p>}
                <button
                    onClick={handleAddStudentToGroup}
                    className="w-full p-2 mt-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
                >
                    Add Student to Group
                </button>
            </div>

            {actionMessage && (
                <div className="mt-4 p-4 bg-yellow-100 text-yellow-800 rounded">
                    <p>{actionMessage}</p>
                </div>
            )}
        </div>
    );
}
