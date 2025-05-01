/** @OnlyCurrentDoc */

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
  },

  // Helper function to find the first active candidate in a row
  firstActiveChoice: function(row, activeCandidates) {
    for (let i = 0; i < row.length; i++) {
      if (activeCandidates.includes(i) && row[i] === 1) {
        return i;
      }
    }
    return -1; // No active candidate found
  }
};

function CalculateWinner() {
  const spreadsheet = SpreadsheetApp.getActive();
  const sheet = spreadsheet.getActiveSheet();

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

  // Track which candidates are still in the race
  let activeCandidates = Array.from({ length: cols }, (_, i) => i); // [0, 1, ..., cols - 1]
  let round = 1;

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
};

function GenerateData() {
  const spreadsheet = SpreadsheetApp.getActive();
  const rows = 100;
  const cols = 5;
  const startRow = 2;
  const startCol = 1;
  let data = Array.from({ length: cols }, (_, i) => i + 1); // [1, 2, ..., cols]

  // Loop each cell from start row/col to end row/col
  for (let i = startRow; i < startRow + rows; i++) {
    utils.shuffleArray(data);
    for (let j = 0; j < cols; j++) {
      let column = j + startCol - 1; // Calculate the column with startCol as the offset
      let cell = utils.cellID(column, i);
      spreadsheet.getRange(cell).setValue(data[j]); // Set the current cell using the shuffled list
    }
  }
};
