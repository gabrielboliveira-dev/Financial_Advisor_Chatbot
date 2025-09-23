from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/analyze/diversification', methods=['POST'])
def analyze_diversification():
    """
    Este endpoint recebe os dados do portfólio do serviço Java.
    Por enquanto, ele apenas imprime os dados recebidos e retorna
    uma resposta fixa (simulada) para que possamos testar a integração.
    """

    portfolio_data = request.json

    print(">>> Análise de diversificação solicitada para o portfólio:", portfolio_data)

    fake_analysis_result = {
      "diversificationBySector": {
        "Tecnologia": 50.0,
        "Financeiro": 30.0,
        "Varejo": 20.0
      }
    }

    return jsonify(fake_analysis_result)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)