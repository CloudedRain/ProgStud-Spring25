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
  }
};

function rcvAlgorithm() {
  var spreadsheet = SpreadsheetApp.getActive();

  // TODO: Add automatic detection for start/end rows/columns
  const rows = 100;
  const cols = 5;
  const startRow = 2; // 1 based row indices
  const startCol = 0; // 0 based column indices

  let votes = Array(cols).fill(0); // Initialize an array of length cols filled with 0s
  let data = []; // Declare data array to store spreadsheet info

  let startID = utils.cellID(startCol, startRow);
  // 0 based columns require the end index to be start + (amount - 1)
  let endID = utils.cellID(startCol + (cols - 1), startRow + rows); 
  data = spreadsheet.getRange(`${startID}:${endID}`).getValues();

  // loop through data array
  for (let i = 0; i < rows; i++) {
    for (let j = 0; j < cols; j++) {
      // increase the current candidate's votes if a first choice is detected
      if (data[i][j] === 1) votes[j]++;
    }
  }

  console.log(votes);
};

function GenerateData() {
  var spreadsheet = SpreadsheetApp.getActive();
  const rows = 100;
  const cols = 5;
  const startRow = 2;
  const startCol = 1;
  let data = Array.from({ length: cols }, (_, i) => i + 1); // cols = 5 -> [1, 2, 3, 4, 5]

  // Loop each cell from start row/col to end row/col
  for (let i = startRow; i <= rows + startRow; i++) {
    utils.shuffleArray(data);
    for (let j = 0; j < cols; j++) {
      let column = j + startCol - 1; // Calculate the column with startCol as the offset
      let cell = utils.cellID(column, i);
      spreadsheet.getRange(cell).setValue(data[j]); // Set the current cell using the shuffled list
    }
  }
};
