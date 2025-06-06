# Programming Studio Journal
Kjeld Nelson\
Spring 2025
## Monday 1/27/25
- [Plan A/B/C for Programming Studio](https://docs.google.com/document/d/1dj5Ut02pOiFnnO70w52i3QF_NHkqCML97zaG4LuyWFc/edit?usp=sharing)
- [Google Sheet for script testing](https://docs.google.com/spreadsheets/d/1Uxt-8pZ6vzg6PlAcBTOrV4rqpeXfcES8H2Hz8TCf1HY/edit?usp=sharing)
- [Sheets API](https://developers.google.com/sheets/api/reference/rest)
- [RCV](https://www.rcvresources.org/how-rcv-works)
## Timeline
**Week of 2/10** -  Study RCV algorithm and Sheets API ✔️\
**Weeks of 2/24-3/3** - Familiarize myself with JavaScript whist developing data generation macro ✔️\
**Week of 3/10** - Begin the RCV algorithm, having it start by tallying up first choice votes ✔️\
\
**Week of 4/7** - Optimise algorithm by pulling data directly into an array and performing array-based calculations ✔️\
**Week of 4/21 (April Break)** - Catch up on lost time and finally add ranked choice functionality to algorithm ✔️\
\
**4/28-4/29 (2 classes)** - Design questionare to collect real-world data ✔️\
**4/30-5/1 (2 classes)** - Release my survey to begin receiving responses ✔️\
\
**Week of 5/5** - Add start/end detection for rows and columns while data trickles in ✔️\
**Week of 5/12** - Polish RCV macro and add faulty-data handling ✔️\
\
**5/21-5/22 (2 classes)** - Cut off data collection and start using my JavaScript macro ✔️\
**5/27-5/29 (3 classes)** - Compile and publish data findings 

## Monday 2/10/25
- [Applying Ranked Choice Voting](https://www.rankedvote.co/guides/applying-ranked-choice-voting/how-to-calculate-ranked-choice-voting-with-google-forms-and-google-sheets)
- I started writing a JavaScript macro to generate test data

## Monday 2/24/25
First day back from break.
```
function GenerateData() {
  const rows = 100;
  const cols = 3;

  var spreadsheet = SpreadsheetApp.getActive();
  for (let i = 0; i < rows; i++) {
    for (let j = 0; j < cols; j++) {
      currentCol = String.fromCharCode("A".charCodeAt(0) + j)
      spreadsheet.getRange('' + currentCol + String.fromCharCode(i)).activate();
      spreadsheet.getCurrentCell().setValue('0');
    }
  }
};
```
A prototype loop for generating data. I'm currently working out a range error and figuring out JavaScript.\
--\
11:50 AM: Solved issues by normalizing my loops to 1 based indexing.
```
function GenerateData() {
  const rows = 100;
  const cols = 3;

  var spreadsheet = SpreadsheetApp.getActive();
  let startRow = 2;
  let startCol = 1;
  for (let i = startRow; i <= rows + startRow; i++) {
    for (let j = startCol-1; j < cols + startCol-1; j++) {
      let cell = String.fromCharCode("A".charCodeAt(0) + j).concat(i);
      spreadsheet.getRange(cell).activate();
      spreadsheet.getCurrentCell().setValue('0');
    }
  }
}; 
```
The macro is now able to automatically set the values of large chunks of the sheet.

## Tuesday 2/25/25
Found an example of a Durstenfeld shuffle algorithm for JavaScript on stack overflow.
```
function shuffleArray(array) {
    for (var i = array.length - 1; i >= 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
```
Successfully implemented this arlgorithm to generate variable amounts of random data.
```
function GenerateData() {
  var spreadsheet = SpreadsheetApp.getActive();
  const rows = 100;
  const cols = 3;
  const startRow = 2;
  const startCol = 1;
  var data = [];

  for (let i = 1; i <= cols; i++) {
    data.push(i);
  }

  for (let i = startRow; i <= rows + startRow; i++) {
    shuffleArray(data);
    for (let j = 0; j < cols; j++) {
      column = j + startCol-1;
      let cell = String.fromCharCode("A".charCodeAt(0) + column).concat(i);
      spreadsheet.getRange(cell).activate();
      spreadsheet.getCurrentCell().setValue(data[j]);
    }
  }
};
```
![Toy Data Sheet](https://github.com/CloudedRain/ProgStud-Spring25/blob/main/Journal%20Images/5x5-data-rcv.png)

## Friday 2/28/25
I met with Ms. Po and she really liked my project!\
She will be talking about it on a radio show featuring pojects at Baxter.\
Her request to me is clear data about the difference in result between ranked-choice and majority vote.\
I also would like to turn my JavaScript macro into some sort of template for easy replication and distribution.

## Monday 3/3/25
Today I emailed Ms. Po about RCV voting, providing her with one of my resources on it and giving a brief explanation in my own words.\
I hope that this will allow her to have a much clearer understanding of my project when she talks about it on Maine Calling.\
**10:50 AM** As a first step in the algorithm, I wrote a messy function to run though the data and count\
the number of 1st choices. This is pretty unoptimised, but progress nonetheless.
```
function rcvAlgorithm() {
  var spreadsheet = SpreadsheetApp.getActive();
  const rows = 100;
  const cols = 5;
  const startRow = 2;
  const startCol = 1;
  var votes = [];
  for (let i = 0; i < cols; i++) {
    votes[i] = 0;
  }
  var choice;

  for (let i = startRow; i <= rows + startRow + 1; i++) {
    for (let j = 0; j < cols; j++) {
      column = j + startCol-1;
      let cell = String.fromCharCode("A".charCodeAt(0) + column).concat(i);
      spreadsheet.getRange(cell).activate();
      if (i == rows + startRow + 1) {
        spreadsheet.getCurrentCell().setValue(votes[j]);
      }
      else {
        choice = spreadsheet.getCurrentCell().getValue();
        console.log(choice);
        if (choice == 1) {
          votes[j] ++;
          console.log(votes[j]);
        }
      }
    }
  }

  spreadsheet.getRange('F2').activate();
  spreadsheet.getCurrentCell().setValue();
};
```
![First Choice Results](https://github.com/CloudedRain/ProgStud-Spring25/blob/main/Journal%20Images/first-choice-results.png)

## Tuesday 4/8/25
I have optimised my functions so that they directly pull values from the spreadsheet instead of having to activate each box.
```
  for (let i = startCol; i < cols + startCol; i++) { 
    votes[i] = 0;
  }

  for (let i = startRow; i <= rows + startRow + 1; i++) {
    for (let j = startCol; j < cols + startCol; j++) {
      if (i == rows + startRow + 1) {
        spreadsheet.getRange(cellID(j, i)).setValue(votes[j]);
      }
      else {
        console.log(j + ", " + cellID(j, i));
        choice = spreadsheet.getRange(cellID(j, i)).getValue(); // Instant getter
        votes[j] += (choice == 1) ? 1 : 0;
      }
    }
  }
};
```
**To do:** Allow the program to detect the start row and column. Tally up non-first choices to apply RCV.
## Spring Break 4/19-4/27
Added a dictionary of helper functions to the beginning of my JS file. This allowed me to remove the functions from top-level scope and hide them from Apps Script.
```
const utils = {
  // Helper function for converting indices to A1 notation
  cellID: function(colIndex, row) {
    let letter = '';
    while (colIndex >= 0) { // Handler for > 26 columns
      letter = String.fromCharCode((colIndex % 26) + 65) + letter;
      colIndex = Math.floor(colIndex / 26) - 1;
    }
    return `${letter}${row}`;
  },

  // Helper function for shuffling arrays
  // Uses a sort of 'reverse bubble sort' algorithm which makes random swaps
  shuffleArray: function(array) {
    for (let i = array.length - 1; i >= 0; i--) {
      let j = Math.floor(Math.random() * (i + 1));
      // Swap selected numbers
      let temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
  }
};
```
\
--\
\
Learned how to shorten this for loop:
```
let data = [];
for (let i = 1; i <= cols; i++) {
  data.push(i);
}
```
Instead use Array.from for a single line.\
`let data = Array.from({ length: cols }, (_, i) => i + 1); // cols = 5 -> [1, 2, 3, 4, 5]`\
\
--\
\
### Finally inditroduced instant runoff functionality!
```
  while (activeCandidates.length > 1) {
    let votes = Array(cols).fill(0);
    let totalVotes = 0;

    // Tally first-choice votes for active candidates
    for (let r = 0; r < rows; r++) {
      const row = voteData[r];
      const choiceIndex = utils.firstActiveChoice(row, activeCandidates);
      if (choiceIndex !== -1) {
        votes[choiceIndex]++;
        totalVotes++;
      }
    }

    console.log(`Round ${round++}:`);
    activeCandidates.forEach(i => {
      console.log(`Candidate ${i + 1}: ${votes[i]} vote(s)`);
    });

    // Check for majority
    for (let i of activeCandidates) {
      if (votes[i] > totalVotes / 2) {
        console.log(`Candidate ${i + 1} wins with majority (${votes[i]}/${totalVotes})`);
        return;
      }
    }

    // Compile least-chosen candidates into an array
    let minVotes = Math.min(...activeCandidates.map(i => votes[i]));
    let lowestCandidates = activeCandidates.filter(i => votes[i] === minVotes);

    if (activeCandidates.length === lowestCandidates.length) {
      // All remaining candidates are tied
      console.log(`Final tie between candidates: ${lowestCandidates.map(i => i + 1).join(" & ")}`);
      return;
    }

    // Eliminate all candidates with the fewest votes
    activeCandidates = activeCandidates.filter(i => !lowestCandidates.includes(i));
    console.log(`Eliminating candidates ${lowestCandidates.map(i => i + 1).join(", ")} with ${minVotes} vote(s)\n`);
  }

  // If this line is reached, only one candidate remains
  console.log(`Candidate ${activeCandidates[0] + 1} wins by default.`);
```
Using a while loop, I continuously tally up the votes before selecting and removing any and all candidates with the least number of votes. I also make sure to check for majority vote and ties to determine the winner(s).\
\
--\
\
Helper function to assist with RCV.
```
  // Helper function to find the first active candidate in a row
  firstActiveChoice: function(row, activeCandidates) {
    for (let i = 0; i < row.length; i++) {
      if (activeCandidates.includes(i) && row[i] === 1) {
        return i;
      }
    }
    return -1; // No active candidate found
  }
```
\
--\
\
Added a primative system to automatically find the range of cells in which the data is contained.
```
  // Get data range dynamically
  const range = sheet.getDataRange();
  const data = range.getValues();

  const startRow = range.getRow();
  const startCol = range.getColumn() - 1; // convert to 0-based
  const rows = range.getNumRows();
  const cols = range.getNumColumns();
```
I did this by directly pulling all the data from the sheet, which excludes blank rows and columns. Once the data is inside an array I no longer need a startRow or startCol.
\
--\
\
Improved my range detection system and made it sort of "smart" by adding a filter for where it starts.
```
  // Get all data from the sheet
  const allData = sheet.getDataRange().getValues();
  if (allData.length < 2) {
    console.log("Not enough data to calculate.");
    return;
  }

  const headers = allData[0];

  // Find the first non-empty header column (assume rankings start here)
  const startCol = headers.findIndex(h => typeof h === 'string' && h.trim() !== "" && h.trim() !== "Timestamp");
  const cols = headers.length - startCol;

  // Slice out just the vote data (ignoring header row and columns before startCol)
  const voteData = allData.slice(1).map(row => row.slice(startCol, startCol + cols));
  const rows = voteData.length;
```
Here you can see I make sure the data is larger than 1 so that there's something to calculate. Then I search for the first column that starts with neither a blank cell or the string "Timestamp" (google forms generates this column when exporting to a spreadsheet).\
The next step to improve this would be doing the same thing for the end column where I make sure there's no blank columns in between, which would tell me that I'm including one or more columns which contain zero data. An alternative method is checking which columns have 0 votes after tallying up the first round, but these dead columns are already taking out in bulk by the group elimination system for all candidates of the same lowest votes.
## Third Check-In
- SUMIF and other functions can be inserted by my JavaScript function to give helpful output and display
- Throughtly test algorithm to make sure it is redistributing votes and comparing a candidates current votes with the initial maximum
- Improve data range detection to check for continuous blocks of data: have thourough algorithms for finding start/end row/column
- Share my survey ASAP to let it collect votes while I make progress

## Tuesday 5/5/25
Fixed RCV logic. The algorithm now eliminates candidates and properly distributes the votes, taking advantage of instant run-off.
```
while (true) {
    let votes = Array(cols).fill(0);
    let totalVotes = 0;

    for (const ballot of ballots) {
      const choice = ballot.find(c => activeCandidates.includes(c));
      if (choice !== undefined) {
        votes[choice]++;
        totalVotes++;
      }
    }

    console.log(`Round ${round}:`);
    activeCandidates.forEach(i => {
      console.log(`${candidateNames[i]}: ${votes[i]} votes`);
    });

    // Output round header
    const roundHeaderCell = sheet.getRange(1, outputCol + round);
    roundHeaderCell.setValue(`Round ${round}`);

    // Find eliminated and possible winner
    const minVotes = Math.min(...activeCandidates.map(i => votes[i]));
    const toEliminate = activeCandidates.filter(i => votes[i] === minVotes);

    let winnerIndex = -1;
    for (const i of activeCandidates) {
      if (votes[i] > totalVotes / 2) {
        winnerIndex = i;
        break;
      }
    }

    // Output vote counts and apply color
    for (const i of activeCandidates) {
      const row = i + 2;
      const cell = sheet.getRange(row, outputCol + round);
      cell.setValue(votes[i]);

      if (i === winnerIndex) {
        cell.setBackground('#c6efce'); // green
      } else if (toEliminate.includes(i)) {
        cell.setBackground('#ffc7ce'); // red
      } else {
        cell.setBackground(null); // clear
      }
    }

    if (winnerIndex !== -1) {
      console.log(`${candidateNames[winnerIndex]} wins with majority (${votes[winnerIndex]}/${totalVotes})`);
      sheet.getRange(1, outputCol + round + 1).setValue(`Winner: ${candidateNames[winnerIndex]}`);
      return;
    }

    if (toEliminate.length === activeCandidates.length) {
      console.log(`Final tie between candidates: ${toEliminate.map(i => candidateNames[i]).join(" & ")}`);
      sheet.getRange(1, outputCol + round + 1).setValue(`Tie: ${toEliminate.map(i => candidateNames[i]).join(" & ")}`);
      return;
    }

    console.log(`Eliminating: ${toEliminate.map(i => candidateNames[i]).join(", ")} with ${minVotes} votes\n`);
    activeCandidates = activeCandidates.filter(i => !toEliminate.includes(i));
    round++;
  }
}
```

## Tuesday 5/12/25
Added faulty data handling by detecting a skipping over rows with duplicate choices.
![Faulty Data Handling](https://github.com/CloudedRain/ProgStud-Spring25/blob/main/Journal%20Images/faulty_data_handling.png)
```
let ballots = [];
  for (let r = 0; r < voteRows.length; r++) {
    const row = voteRows[r].slice(startCol, startCol + cols);

    // Check for duplicate rankings
    const ranksSeen = new Set();
    let faulty = false;
    for (const val of row) {
      if (typeof val === 'number') {
        if (ranksSeen.has(val)) {
          faulty = true;
          break;
        }
        ranksSeen.add(val);
      }
    }

    if (faulty) {
      console.log(`Skipping row ${r + 2} due to duplicate rankings: [${row}]`);
      continue;
    }

    const sorted = row
      .map((rank, i) => ({ candidate: i, rank }))
      .filter(cell => typeof cell.rank === 'number' && !isNaN(cell.rank))
      .sort((a, b) => a.rank - b.rank)
      .map(cell => cell.candidate);

    ballots.push(sorted);
  }
```

## Wednesday 5/27/25
Added informative output for each round color coded based on who was eliminated and who won.
![Color Coded Output](https://github.com/CloudedRain/ProgStud-Spring25/blob/main/Journal%20Images/color_coded_results.png)
