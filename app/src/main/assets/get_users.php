<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type");

// Configurações de conexão com o banco de dados
$servername = "localhost";
$username = "root"; // Usuário padrão do MySQL no XAMPP
$password = "";     // Senha padrão é vazia
$dbname = "seu_banco_de_dados"; // Substitua pelo nome do seu banco de dados

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode([
        'success' => false,
        'message' => 'Conexão falhou: ' . $conn->connect_error
    ]));
}

$email = $_GET['email'];
$password = $_GET['password'];

$email = filter_var($email, FILTER_SANITIZE_EMAIL);
$password = filter_var($password, FILTER_SANITIZE_STRING);

$sql = "SELECT * FROM users WHERE email = ? AND password = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $email, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode([
        'success' => true,
        'message' => 'Login bem-sucedido'
    ]);
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Email ou senha incorretos'
    ]);
}

$stmt->close();
$conn->close();
?>
