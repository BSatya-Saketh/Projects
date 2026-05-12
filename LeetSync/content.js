console.log("LeetSync: Content script successfully loaded on LeetCode!");

const startObserver = () => {
    const observer = new MutationObserver((mutations) => {
        for (const mutation of mutations) {
            // Step 1: Detect that the problem is successfully completed
            if (document.body.innerText.includes("Accepted")) {
                console.log("LeetSync: SUCCESS DETECTED! Waiting for code editor to settle...");
                observer.disconnect(); 
                
                // Step 2: The "Wait Until Settled" Loop
                let attempts = 0;
                const waitForCode = setInterval(() => {
                    const codeElements = document.querySelectorAll('.view-line');
                    const code = Array.from(codeElements).map(el => el.innerText).join('\n').trim();
                    attempts++;
                    
                    // If the code is longer than 5 characters, we know the editor has fully loaded
                    if (code.length > 5) { 
                        clearInterval(waitForCode); // Stop checking
                        console.log("LeetSync: Editor settled and code found! Extracting data...");
                        extractData(); // Push to GitHub!
                    } 
                    // Safety Net: Stop checking after 10 seconds (20 attempts * 500ms)
                    else if (attempts > 20) { 
                        clearInterval(waitForCode);
                        console.error("LeetSync: Editor took too long to load. Please use Force Sync.");
                    }
                }, 500); // Check every half-second
                
                return; 
            }
        }
    });

    observer.observe(document.body, { childList: true, subtree: true });
};

const extractData = () => {
    const title = document.title.split('-')[0].trim();

    const codeElements = document.querySelectorAll('.view-line');
    const code = Array.from(codeElements).map(el => el.innerText).join('\n');

    // --- UPGRADE 1: Grab innerHTML to keep all LeetCode formatting! ---
    const descElement = document.querySelector('[data-track-load="description_content"]');
    const description = descElement ? descElement.innerHTML : "Description not found";

    // --- UPGRADE 2: Grab the exact URL ---
    const problemUrl = window.location.href;

    // Extract Difficulty
    let difficulty = "Uncategorized";
    if (document.querySelector('.text-difficulty-easy')) difficulty = "Easy";
    else if (document.querySelector('.text-difficulty-medium')) difficulty = "Medium";
    else if (document.querySelector('.text-difficulty-hard')) difficulty = "Hard";

    // Extract Language
    const editorElement = document.querySelector('[data-mode-id]');
    const rawLanguage = editorElement ? editorElement.getAttribute('data-mode-id') : 'java';
    
    let languageFolder = "Unknown";
    let fileExtension = ".txt";

    if (rawLanguage.includes("java")) {
        languageFolder = "Java";
        fileExtension = ".java";
    } else if (rawLanguage.includes("python")) {
        languageFolder = "Python";
        fileExtension = ".py";
    } else if (rawLanguage.includes("cpp") || rawLanguage.includes("c++")) {
        languageFolder = "C++";
        fileExtension = ".cpp";
    } else {
        languageFolder = rawLanguage.charAt(0).toUpperCase() + rawLanguage.slice(1);
        fileExtension = `.${rawLanguage}`; 
    }

    // Extract Tags (Topics)
    const tagElements = document.querySelectorAll('a[href^="/tag/"]');
    const tagsArray = Array.from(tagElements).map(el => el.innerText.trim());
    const tags = tagsArray.length > 0 ? tagsArray.join(', ') : "No tags found";

    // Extract Metrics
    let runtime = "N/A";
    let memory = "N/A";
    const statElements = document.querySelectorAll('.text-sd-green-s'); 
    if (statElements.length >= 2) {
        runtime = statElements[0].innerText;
        memory = statElements[1].innerText;
    } else if (document.body.innerText.includes("Beats")) {
        runtime = "Successfully Evaluated";
    }

    console.log(`LeetSync: Scraped DOM. Diff: ${difficulty}, Lang: ${languageFolder}`);

    chrome.runtime.sendMessage({
        type: "SUBMISSION_ACCEPTED",
        payload: {
            platform:"LeetCode",
            title: title,
            problemUrl: problemUrl, // Send URL to background
            description: description,
            code: code,
            difficulty: difficulty,
            languageFolder: languageFolder,
            fileExtension: fileExtension,
            tags: tags,         
            runtime: runtime,   
            memory: memory      
        }
    });
};

// Start looking for "Accepted" 2 seconds after the page initially loads
setTimeout(startObserver, 2000);

// Listen for the manual sync button from the popup
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.action === "FORCE_SYNC") {
        console.log("LeetSync: Manual sync triggered by user!");
        extractData(); 
        sendResponse({status: "success"});
    }
});