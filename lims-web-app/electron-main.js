const { app, BrowserWindow } = require("electron");
const path = require("path");
const express = require("express");
const cors = require("cors");
const localServerApp = express();
const PORT = 3000;

// Start Express server
const startLocalServer = (done) => {
  localServerApp.use(express.json({ limit: "100mb" }));
  localServerApp.use(cors());
  localServerApp.use(express.static(path.resolve(__dirname, "build"))); // Fix the static path
  localServerApp.listen(PORT, async () => {
    console.log("Server Started on PORT ", PORT);
    if (done) done(); // Ensure the callback is invoked
  });
};

// Create Electron Window
function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false, // Fix for allowing Node integration
    },
  });

  mainWindow.loadURL(`http://localhost:${PORT}`); // Load from Express server
  mainWindow.webContents.openDevTools(); // Open DevTools for debugging
}

// Initialize Electron app
app.whenReady().then(() => {
  startLocalServer(createWindow); // Start server, then create window

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

// Handle app closure
app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});


app.on('ready', () => {
  process.env.ELECTRON_DISABLE_SECURITY_WARNINGS = 'true'; // Suppress security warnings
  console.log = function () {}; // Suppress all logs
});
