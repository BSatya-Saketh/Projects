const statusDiv = document.getElementById("status");

// --- REPOSITORY DETAILS ---
const REPO_OWNER = "BSatya-Saketh"; 
const REPO_NAME = "Leetcode_Solutions";
const BRANCH = "main"; // Change to "master" if your GitHub branch is named master
// --------------------------

function showMessage(text, isError = false) {
    statusDiv.innerText = text;
    statusDiv.className = isError ? "error" : "success";
    setTimeout(() => { statusDiv.innerText = ""; }, 3000);
}

// NEW: Function to fetch the actual count from GitHub
async function syncCountWithGitHub(token) {
    try {
        const url = `https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/git/trees/${BRANCH}?recursive=1`;
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Accept': 'application/vnd.github.v3+json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            
            // Count every README.md file in the repository
            const actualProblemCount = data.tree.filter(item => item.path.endsWith('README.md')).length;
            
            const countElement = document.getElementById("streakCount");
            countElement.innerText = actualProblemCount;
            
            // Auto-correct the local storage to match GitHub's reality
            chrome.storage.local.set({ syncCount: actualProblemCount });
            
            console.log(`LeetSync: True count synced from GitHub -> ${actualProblemCount}`);
        }
    } catch (error) {
        console.error("LeetSync: Failed to fetch true count from GitHub", error);
    }
}

// 1. Save Token Logic
document.getElementById("save").addEventListener("click", () => {
    const token = document.getElementById("token").value.trim();

    if (!token) {
        showMessage("Please enter a valid token!", true);
        return;
    }

    chrome.storage.local.set({ githubToken: token }, () => {
        showMessage("Token saved successfully!");
        document.getElementById("token").value = ""; 
        document.getElementById("token").placeholder = "Token is securely saved";
        
        // Fetch the true count immediately after saving a new token
        syncCountWithGitHub(token);
    });
});

// 2. Force Sync Logic
document.getElementById("forceSync").addEventListener("click", () => {
    const syncBtn = document.getElementById("forceSync");
    syncBtn.innerText = "Syncing...";
    syncBtn.style.opacity = "0.7";

    chrome.tabs.query({active: true, currentWindow: true}, (tabs) => {
        if (!tabs[0].url.includes("leetcode.com/problems")) {
            showMessage("Please open a LeetCode problem first!", true);
            syncBtn.innerText = "Sync Current Problem";
            syncBtn.style.opacity = "1";
            return;
        }

        chrome.tabs.sendMessage(tabs[0].id, { action: "FORCE_SYNC" }, (response) => {
            syncBtn.innerText = "Sync Current Problem";
            syncBtn.style.opacity = "1";
            
            if (chrome.runtime.lastError) {
                showMessage("Please refresh the LeetCode page and try again.", true);
            } else {
                showMessage("Pushed to GitHub!");
            }
        });
    });
});

// 3. On Load: Check token, set UI, and trigger GitHub True Sync
chrome.storage.local.get(["githubToken", "syncCount"], (result) => {
    if (result.githubToken) {
        document.getElementById("token").placeholder = "Token is securely saved";
        document.getElementById("save").innerText = "Update Token";
        
        // Ask GitHub for the exact file count every time the popup opens
        syncCountWithGitHub(result.githubToken);
    }
    
    // Display the last known local count instantly so the UI doesn't look broken while fetching
    if (result.syncCount !== undefined) {
        document.getElementById("streakCount").innerText = result.syncCount;
    }
});

// 4. Listen for live background changes
chrome.storage.onChanged.addListener((changes, namespace) => {
    if (namespace === 'local' && changes.syncCount) {
        const countElement = document.getElementById("streakCount");
        countElement.innerText = changes.syncCount.newValue;
        
        countElement.style.color = "#ffffff";
        setTimeout(() => { countElement.style.color = "#2ea043"; }, 300);
    }
});