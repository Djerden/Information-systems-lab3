import React, { useState } from "react";
import Modal from "react-modal";

Modal.setAppElement("#root");

export default function ImportFilesModal({ isOpen, onRequestClose, onFilesImported }) {
    const [file, setFile] = useState(null); // Хранение файла
    const [error, setError] = useState(""); // Хранение сообщения об ошибке
    const [progress, setProgress] = useState(0); // Хранение прогресса загрузки
    const token = sessionStorage.getItem("jwt");

    function resetForm() {
        setFile(null);
        setError("");
        setProgress(0);
    }

    async function handleImport() {
        try {
            if (!file) {
                setError("Please select a file to import.");
                return;
            }

            const formData = new FormData();
            formData.append("file", file);

            const response = await fetch("http://localhost:8080/import/yaml", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                body: formData,
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Failed to import file.");
            }

            resetForm();
            onRequestClose();
            if (onFilesImported) onFilesImported();
        } catch (error) {
            setError(error.message);
        }
    }

    function handleFileSelection(event) {
        const selectedFile = event.target.files[0];

        if (!selectedFile) {
            setError("Please select a valid file.");
            return;
        }

        // Проверяем расширение файла
        const validExtensions = [".yaml", ".yml"];
        const fileName = selectedFile.name.toLowerCase();
        const hasValidExtension = validExtensions.some((ext) => fileName.endsWith(ext));

        if (!hasValidExtension) {
            setError("Only YAML files are allowed.");
            return;
        }

        setFile(selectedFile);
        setError("");
    }

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={() => {
                resetForm();
                onRequestClose();
            }}
            contentLabel="Import Files"
            className="bg-white p-6 rounded shadow-md max-w-md mx-auto mt-20"
        >
            <h2 className="text-lg font-bold mb-4">Import YAML File</h2>

            {/* Сообщение об ошибке */}
            {error && <div className="mb-4 text-red-500 font-medium">{error}</div>}

            {/* Поле для выбора файла */}
            <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    Select a YAML File
                </label>
                <input
                    type="file"
                    accept=".yml,.yaml"
                    onChange={handleFileSelection}
                    className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
                {file && (
                    <div className="mt-2 text-sm text-gray-600">Selected file: {file.name}</div>
                )}
            </div>

            {/* Прогресс загрузки */}
            {progress > 0 && (
                <div className="mb-4">
                    <div className="h-2 bg-gray-200 rounded">
                        <div
                            className="h-2 bg-indigo-500 rounded"
                            style={{ width: `${progress}%` }}
                        ></div>
                    </div>
                    <div className="text-sm text-gray-600 mt-1">{progress}% uploaded</div>
                </div>
            )}

            <div className="flex justify-end">
                <button
                    onClick={() => {
                        resetForm();
                        onRequestClose();
                    }}
                    className="px-4 py-2 bg-gray-300 text-gray-700 rounded mr-2"
                >
                    Cancel
                </button>
                <button
                    onClick={handleImport}
                    className="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600"
                    disabled={!file}
                >
                    Import
                </button>
            </div>
        </Modal>
    );
}