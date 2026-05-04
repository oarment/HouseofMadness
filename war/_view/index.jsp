<!DOCTYPE html>
<html>
<head>
    <title>Whispers of the Hollow</title>

    <style>
        body {
            background-color: #2e2d30;
            color: #00ff1e;
            font-family: "Courier New", monospace;
            text-align: center;
            margin: 0;
            padding-top: 80px;
        }

        h1 {
            font-size: 48px;
            margin-bottom: 20px;
        }

        .subtitle {
            color: #ffff66;
            font-size: 20px;
            margin-bottom: 35px;
        }

        button {
            padding: 14px 30px;
            font-size: 18px;
            margin: 10px;
            cursor: pointer;
            background-color: #1b1b1d;
            color: #00ff1e;
            border: 1px solid #00ff1e;
            font-family: "Courier New", monospace;
        }

        button:hover {
            background-color: #00ff1e;
            color: #1b1b1d;
        }

        .room-preview {
            margin: 35px auto 0 auto;
            width: 520px;
            height: 280px;
            border: 2px solid #00ff1e;
            background-color: #1b1b1d;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .room-preview img {
            max-width: 500px;
            max-height: 260px;
            object-fit: contain;
        }

        .room-label {
            margin-top: 12px;
            color: #ffff66;
            font-size: 18px;
        }
    </style>
</head>

<body>
    <h1>Welcome to The Hollow</h1>
    <div class="subtitle">Continue your nightmare, or begin again.</div>

    <form action="${pageContext.request.contextPath}/index" method="post">
        <button type="submit" name="action" value="continue">Continue Game</button>
        <button type="submit" name="action" value="new">New Game</button>
    </form>

    <div class="room-preview">
        <img src="${pageContext.request.contextPath}/static/images/rooms/${lastRoomImage}"
             alt="${lastRoomName}" />
    </div>

    <div class="room-label">
        Last Location: ${lastRoomName}
    </div>
</body>
</html>