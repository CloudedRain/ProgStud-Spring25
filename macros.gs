/** @OnlyCurrentDoc */

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
