export const getChartsConf = function (type, linesInfo) {
  switch (type) {
    case 'pie':
      return PIE_SET;
    case 'horizontalBar':
      return horizontalBar(linesInfo);
    case 'bar':
      return BAR;
  }
};


const PIE_SET = {
  type: "pie",
  data: {
    labels: ["成功数", "失败数"],
    datasets: [
      {
        label: "# of Votes",
        data: [],
        backgroundColor: [
          "rgba(15, 210, 255, 0.8)",
          "rgba(255, 255, 153, 0.8)",
        ],
        borderColor: [
          "rgba(15, 210, 255, 1)",
          "rgba(255, 255, 153, 1)",
        ],
        borderWidth: 3
      }
    ]
  },
  options: {
    responsive: true,
    devicePixelRatio: 2,
    cutoutPercentage: 0,
    showAllTooltips: true,
    title: {
      fontColor: '#fff',
      fontSize: 14,
      display: true,
      text: ''
    },
    legend: {
      display: false
    },
    layout: {
      padding: {
        left: 0
      }
    }
  }
};

export const horizontalBar = function (linesInfo) {
  console.log(linesInfo);
  let lines = [];
  for (let i = 0; i < linesInfo.length; i++) {
    lines.push(linesInfo[i].line);
  }
  const HORIZONTAL_BAR = {
    type: 'horizontalBar',
    data: {
      labels: lines,
      datasets: [
        {
          label: '达成率',
          data: [],
          backgroundColor: "rgba(15, 210, 255, 0.8)",
          borderColor: "rgba(15, 210, 255, 1)",
          borderWidth: 3
        }
      ]
    },
    options: {
      responsive: true,
      devicePixelRatio: 2,
      cutoutPercentage: 0,
      showAllTooltips: true,
      title: {
        fontColor: '#fff',
        fontSize: 14,
        display: true,
        text: ''
      },
      legend: {
        display: false
      },
      layout: {
        padding: {
          left: 0
        },
      },
      scales: {
        xAxes: [{
          ticks: {
            display: true,
            beginAtZero: true,
            fontColor: '#ddd'
          },
          scaleLabel: {
            display: true,
            labelString: "百分比(%)",
            fontColor: '#ddd'
          },
          gridLines: {
            color: '#ddd'
          }
        }],
        yAxes: [{
          ticks: {
            fontColor: '#ddd'
          },
          gridLines: {
            color: 'rgba(128,128,128,0.8)'
          }
        }]
      },
    }
  };
  return HORIZONTAL_BAR;
}

const BAR = {
  type: 'bar',
  data: {
    labels: [],
    datasets: [
      {
        label: '上料成功数',
        data: [10],
        stack: 'Stack 0',
        backgroundColor: "rgba(93, 214, 223, 1)",
        borderWidth: 0
      },
      {
        label: '上料失败数',
        data: [1],
        stack: 'Stack 0',
        backgroundColor: "rgba(255, 228, 156, 1)",
        borderWidth: 0
      },
      {
        label: '换料成功数',
        data: [12],
        stack: 'Stack 1',
        backgroundColor: "rgba(178, 212, 255, 1)",
        borderWidth: 0
      },
      {
        label: '换料失败数',
        data: [1],
        stack: 'Stack 1',
        backgroundColor: "rgba(255, 250, 134, 1)",
        borderWidth: 0
      },
      {
        label: '核料成功数',
        data: [],
        stack: 'Stack 2',
        backgroundColor: "rgba(93, 214, 223, 1)",
        borderWidth: 0
      },
      {
        label: '核料失败数',
        data: [],
        stack: 'Stack 2',
        backgroundColor: "rgba(255, 228, 156, 1)",
        borderWidth: 0
      },
      {
        label: '全检成功数',
        data: [],
        stack: 'Stack 3',
        backgroundColor: "rgba(178, 212, 255, 1)",
        borderWidth: 0
      },
      {
        label: '全检失败数',
        data: [],
        stack: 'Stack 3',
        backgroundColor: "rgba(255, 250, 134, 1)",
        borderWidth: 0
      }
    ]
  },
  options: {
    responsive: true,
    devicePixelRatio: 2,
    cutoutPercentage: 0,
    showAllTooltips: false,
    title: {
      fontColor: '#fff',
      fontSize: 20,
      display: true,
      text: ''
    },
    legend: {
      display: false
    },
    layout: {
      padding: {
        left: 0
      },
    },
    scales: {
      xAxes: [{
        ticks: {
          display: true,
          beginAtZero: true,
          fontColor: '#ddd'
        },
        scaleLabel: {
          display: true,
          fontColor: '#ddd',
          labelString: "| 上料 | 换料 | 核料 | 全检 |",
        },
        gridLines: {
          color: '#ddd'
        },
        stacked: true
      }],
      yAxes: [{
        ticks: {
          fontColor: '#ddd',
          beginAtZero: true,
        },
        gridLines: {
          color: 'rgba(128,128,128,0.8)'
        },
        stacked: true
      }]
    },
  }
};
