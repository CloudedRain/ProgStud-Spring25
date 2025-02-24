# Programing Studio Journal
Kjeld Nelson\
Spring 2025
## Monday 1/27/25
- [Plan A/B/C for Programing Studio](https://docs.google.com/document/d/1dj5Ut02pOiFnnO70w52i3QF_NHkqCML97zaG4LuyWFc/edit?usp=sharing)
- [Google Sheet for script testing](https://docs.google.com/spreadsheets/d/1Uxt-8pZ6vzg6PlAcBTOrV4rqpeXfcES8H2Hz8TCf1HY/edit?usp=sharing)
- [Sheets API](https://developers.google.com/sheets/api/reference/rest)
- [RCV](https://www.rcvresources.org/how-rcv-works)
## Timeline
**Week of 2/10** -  Study RCV algorithm and Sheets API while writing macro to generate test data\
**Weeks of 2/24-3/3** - Begin writing JavaScript code in Sheets\
**Week of 3/10** - Design questionare to collect real-world data\
**3/17-3/18 (2 classes)** - Send out my survey to begin receiving responses\
**3/20-3/28 (7 classes)** - Polish JavaScript macro and test with toy data\
**Week of 3/31** - Cut off data collection and start using my JavaScript macro\
**Week of 4/7** - 

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
      console.log(cell);
      spreadsheet.getRange(cell).activate();
      spreadsheet.getCurrentCell().setValue('0');
    }
  }
};
```
The macro is now able to automatically set the values of large chunks of the sheet.\
[]()

