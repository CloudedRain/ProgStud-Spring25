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
};

function CalculateWinner() {
  const spreadsheet = SpreadsheetApp.getActive();
  const sheet = spreadsheet.getActiveSheet();

  const allData = sheet.getDataRange().getValues();
  if (allData.length < 2) {
    console.log("Not enough data to calculate.");
    return;
  }

  const headers = allData[0];
  const startCol = headers.findIndex(h => typeof h === 'string' && h.trim() !== "" && h.trim() !== "Timestamp");
  const cols = headers.length - startCol;
  const voteRows = allData.slice(1);
  const candidateNames = headers.slice(startCol, startCol + cols);

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

  if (ballots.length === 0) {
    console.log("No valid ballots remaining after filtering.");
    return;
  }

  let activeCandidates = Array.from({ length: cols }, (_, i) => i);
  let round = 1;

  // Find first empty column for results
  let outputCol = headers.length;
  while (sheet.getRange(1, outputCol + 1).getValue() !== "") {
    outputCol++;
  }

  // Output candidate names in rows
  candidateNames.forEach((name, i) => {
    sheet.getRange(i + 2, outputCol + 1).setValue(name);
  });

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
